package com.imyvm.adventure.application.listener

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.api.event.AerialEvents
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.domain.model.ActionClass
import com.imyvm.adventure.domain.model.ActionEventType
import com.imyvm.adventure.infra.config.EconomyConfig
import com.imyvm.adventure.infra.AdventureDatabase
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Leashable
import net.minecraft.world.entity.animal.happyghast.HappyGhast
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import java.util.UUID

class GhastListener {
    private val lastHaulPos: MutableMap<UUID, Triple<Double, Double, Double>> = mutableMapOf()

    fun register() {
        AttackEntityCallback.EVENT.register { player, _, _, target, _ ->
            if (player is ServerPlayer && target is LivingEntity && target !is Player) {
                if (player.vehicle is HappyGhast) {
                    AerialEvents.ON_AIR_HIT.invoker().onAirHit(player, target)
                }
            }
            InteractionResult.PASS
        }

        AerialEvents.ON_AIR_HIT.register { player, victim ->
            payAerial(player, ActionEventType.AIR_HIT) { logFields ->
                WorldGeoAdventureAddon.logger.info(
                    "[adventure.aerial] event=air_hit player={} victim={} {}",
                    player.scoreboardName, victim.type.description, logFields
                )
            }
        }

        ServerTickEvents.END_SERVER_TICK.register { server ->
            if (!AdventureServices.isReady()) return@register
            if (server.tickCount % HAUL_CHECK_INTERVAL_TICKS != 0) return@register
            val threshold = EconomyConfig.AIR_HAUL_DISTANCE_THRESHOLD.value.toDouble()
            for (player in server.playerList.players) {
                checkHaul(player, threshold)
            }
        }

        AerialEvents.ON_AIR_HAUL.register { player, distance ->
            payAerial(player, ActionEventType.AIR_HAUL) { logFields ->
                WorldGeoAdventureAddon.logger.info(
                    "[adventure.aerial] event=air_haul player={} distance={} {}",
                    player.scoreboardName, distance, logFields
                )
            }
        }
    }

    private fun checkHaul(player: ServerPlayer, threshold: Double) {
        val ghast = player.vehicle as? HappyGhast
        val id = player.uuid
        if (ghast == null) {
            lastHaulPos.remove(id)
            return
        }
        if (!hasLeashedFollowerOwnedBy(player)) {
            lastHaulPos.remove(id)
            return
        }
        val current = Triple(player.x, player.y, player.z)
        val last = lastHaulPos[id]
        if (last == null) {
            lastHaulPos[id] = current
            return
        }
        val dx = current.first - last.first
        val dy = current.second - last.second
        val dz = current.third - last.third
        val dist = kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
        if (dist >= threshold) {
            lastHaulPos[id] = current
            AerialEvents.ON_AIR_HAUL.invoker().onAirHaul(player, dist)
        }
    }

    private fun hasLeashedFollowerOwnedBy(player: ServerPlayer): Boolean {
        val level = player.level() as? ServerLevel ?: return false
        val box = AABB.ofSize(player.position(), LEASH_SCAN_RADIUS * 2.0, LEASH_SCAN_RADIUS * 2.0, LEASH_SCAN_RADIUS * 2.0)
        val candidates = level.getEntitiesOfClass(LivingEntity::class.java, box) { it is Leashable }
        for (entity in candidates) {
            val leashable = entity as Leashable
            if (leashable.leashHolder === player) return true
        }
        return false
    }

    private inline fun payAerial(player: ServerPlayer, eventType: ActionEventType, log: (String) -> Unit) {
        val location = AdventureServices.scopeResolver.resolveAdventureLocation(player)
        if (location == null) {
            WorldGeoAdventureAddon.logger.debug(
                "[adventure.aerial] event={} player={} outside adventure scope",
                eventType.configKey, player.scoreboardName
            )
            return
        }
        val actionClass = ActionClass.AERIAL
        if (AdventureServices.sessionManager.shouldSuppress(
                player, eventType, location.region.numberID, AdventureServices.scheduleService.totalTicks()
            )
        ) {
            WorldGeoAdventureAddon.logger.debug(
                "[adventure.aerial] event={} player={} suppressed",
                eventType.configKey, player.scoreboardName
            )
            return
        }
        val alpha = EconomyConfig.allowanceFractionFor(actionClass)
        val baseScore = EconomyConfig.baseScoreFor(eventType)
        val classWeight = EconomyConfig.classWeightFor(actionClass)
        val phaseWeight = MoonPhase.currentWeight()
        val heatPenalty = HEAT_PENALTY_PLACEHOLDER
        val opScore = baseScore * classWeight * phaseWeight * (1.0 - heatPenalty)
        val allowance = alpha * opScore
        val amount = (allowance * 100.0).toLong()
        val deposited = AdventureServices.economyBridgeService.deposit(player, amount)
        AdventureDatabase.state.addDailyOperationScore(
            player.uuid, actionClass, AdventureServices.scheduleService.today(), opScore
        )
        log("region=${location.region.name} alpha=$alpha base=$baseScore cw=$classWeight pw=$phaseWeight hp=$heatPenalty op=$opScore allowance=$allowance amount=$amount deposited=$deposited")
    }

    companion object {
        private const val HEAT_PENALTY_PLACEHOLDER: Double = 0.0
        private const val HAUL_CHECK_INTERVAL_TICKS: Int = 20
        private const val LEASH_SCAN_RADIUS: Double = 8.0
    }
}

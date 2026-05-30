package com.imyvm.adventure.application.listener

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.domain.model.ActionClass
import com.imyvm.adventure.domain.model.ActionEventType
import com.imyvm.adventure.infra.config.EconomyConfig
import com.imyvm.adventure.infra.AdventureDatabase
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

class CombatListener {
    fun register() {
        ServerLivingEntityEvents.AFTER_DEATH.register { victim, damageSource ->
            if (victim is Player) return@register
            val attacker = damageSource.entity as? ServerPlayer ?: return@register

            val location = AdventureServices.scopeResolver.resolveAdventureLocation(attacker)
            if (location == null) {
                WorldGeoAdventureAddon.logger.debug(
                    "[adventure.combat] player={} outside adventure scope",
                    attacker.scoreboardName
                )
                return@register
            }

            val eventType = ActionEventType.COMBAT
            val actionClass = ActionClass.COMBAT
            val alpha = EconomyConfig.allowanceFractionFor(actionClass)
            val baseScore = EconomyConfig.baseScoreFor(eventType)
            val classWeight = EconomyConfig.classWeightFor(actionClass)
            val phaseWeight = MoonPhase.currentWeight()
            val heatPenalty = HEAT_PENALTY_PLACEHOLDER
            val opScore = baseScore * classWeight * phaseWeight * (1.0 - heatPenalty)
            val allowance = alpha * opScore
            val amount = (allowance * 100.0).toLong()
            val deposited = AdventureServices.economyBridgeService.deposit(attacker, amount)
            AdventureDatabase.state.addDailyOperationScore(
                attacker.uuid, actionClass, AdventureServices.scheduleService.today(), opScore
            )

            WorldGeoAdventureAddon.logger.info(
                "[adventure.combat] player={} region={} victim={} alpha={} base={} cw={} pw={} hp={} op={} allowance={} amount={} deposited={}",
                attacker.scoreboardName,
                location.region.name,
                victim.type.descriptionId,
                alpha,
                baseScore,
                classWeight,
                phaseWeight,
                heatPenalty,
                opScore,
                allowance,
                amount,
                deposited
            )
        }
    }

    companion object {
        private const val HEAT_PENALTY_PLACEHOLDER: Double = 0.0
    }
}

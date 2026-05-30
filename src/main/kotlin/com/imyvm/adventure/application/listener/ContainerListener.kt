package com.imyvm.adventure.application.listener

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.api.event.ChestEvents
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.domain.model.ActionClass
import com.imyvm.adventure.domain.model.ActionEventType
import com.imyvm.adventure.infra.config.EconomyConfig
import com.imyvm.adventure.infra.AdventureDatabase
import net.minecraft.server.level.ServerLevel

class ContainerListener {
    fun register() {
        ChestEvents.ON_CHEST_LOOT_UNPACKED.register { player, container ->
            val location = AdventureServices.scopeResolver.resolveAdventureLocation(player)
            if (location == null) {
                WorldGeoAdventureAddon.logger.debug(
                    "[adventure.cache] player={} outside adventure scope",
                    player.scoreboardName
                )
                return@register
            }

            val level = player.level() as? ServerLevel
            val isRareCache = level != null &&
                AdventureServices.rareCacheService.activeAt(level, container.blockPos) != null
            val eventType = if (isRareCache) ActionEventType.RARE_CACHE else ActionEventType.CHEST
            val actionClass = ActionClass.CACHE
            val nowTick = AdventureServices.scheduleService.totalTicks()
            val heatPenalty = AdventureServices.sessionManager.heatPenalty(player, eventType, nowTick)
            if (AdventureServices.sessionManager.shouldSuppress(
                    player, eventType, location.region.numberID, nowTick
                )
            ) {
                WorldGeoAdventureAddon.logger.debug(
                    "[adventure.cache] player={} suppressed eventType={}",
                    player.scoreboardName, eventType.configKey
                )
                return@register
            }
            val alpha = EconomyConfig.allowanceFractionFor(actionClass)
            val baseScore = EconomyConfig.baseScoreFor(eventType)
            val classWeight = EconomyConfig.classWeightFor(actionClass)
            val phaseWeight = MoonPhase.currentWeight()
            val opScore = baseScore * classWeight * phaseWeight * (1.0 - heatPenalty)
            val allowance = alpha * opScore
            val amount = (allowance * 100.0).toLong()
            val deposited = AdventureServices.economyBridgeService.deposit(player, amount)
            AdventureDatabase.state.addDailyOperationScore(
                player.uuid, actionClass, AdventureServices.scheduleService.today(), opScore
            )

            WorldGeoAdventureAddon.logger.info(
                "[adventure.cache] event={} player={} region={} lootTable={} pos={} alpha={} base={} cw={} pw={} hp={} op={} allowance={} amount={} deposited={}",
                eventType.configKey,
                player.scoreboardName,
                location.region.name,
                container.lootTable,
                container.blockPos,
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
}

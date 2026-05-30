package com.imyvm.adventure.application.listener

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.api.event.BrushEvents
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.domain.model.ActionClass
import com.imyvm.adventure.domain.model.ActionEventType
import com.imyvm.adventure.infra.config.EconomyConfig
import com.imyvm.adventure.infra.AdventureDatabase

class SampleBlockListener {
    fun register() {
        BrushEvents.ON_BRUSHING_COMPLETED.register { player, _, blockEntity ->
            val location = AdventureServices.scopeResolver.resolveAdventureLocation(player)
            if (location == null) {
                WorldGeoAdventureAddon.logger.debug(
                    "[adventure.sample] player={} outside adventure scope",
                    player.scoreboardName
                )
                return@register
            }

            val eventType = ActionEventType.BRUSH
            val actionClass = ActionClass.SAMPLE
            val nowTick = AdventureServices.scheduleService.totalTicks()
            val heatPenalty = AdventureServices.sessionManager.heatPenalty(player, eventType, nowTick)
            if (AdventureServices.sessionManager.shouldSuppress(
                    player, eventType, location.region.numberID, nowTick
                )
            ) {
                WorldGeoAdventureAddon.logger.debug(
                    "[adventure.sample] player={} suppressed eventType={}",
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
                "[adventure.sample] event={} player={} region={} pos={} alpha={} base={} cw={} pw={} hp={} op={} allowance={} amount={} deposited={}",
                eventType.configKey,
                player.scoreboardName,
                location.region.name,
                blockEntity.blockPos,
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

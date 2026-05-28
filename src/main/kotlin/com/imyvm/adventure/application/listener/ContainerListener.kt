package com.imyvm.adventure.application.listener

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.api.event.ChestEvents
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.domain.model.ActionClass
import com.imyvm.adventure.domain.model.ActionEventType
import com.imyvm.adventure.infra.config.EconomyConfig

class ContainerListener {
    fun register() {
        ChestEvents.ON_CHEST_LOOT_UNPACKED.register { player, container ->
            val location = AdventureServices.scopeResolver.resolveAdventureLocation(player)
            if (location == null) {
                WorldGeoAdventureAddon.logger.debug(
                    "[adventure.chest] player={} outside adventure scope",
                    player.scoreboardName
                )
                return@register
            }

            val eventType = ActionEventType.CHEST
            val actionClass = ActionClass.CACHE
            val alpha = EconomyConfig.allowanceFractionFor(actionClass)
            val baseScore = EconomyConfig.baseScoreFor(eventType)
            val classWeight = EconomyConfig.classWeightFor(actionClass)
            val phaseWeight = MoonPhase.currentWeight()
            val heatPenalty = HEAT_PENALTY_PLACEHOLDER
            val allowance = alpha * baseScore * classWeight * phaseWeight * (1.0 - heatPenalty)
            val amount = (allowance * 100.0).toLong()
            val deposited = AdventureServices.economyBridgeService.deposit(player, amount)

            WorldGeoAdventureAddon.logger.info(
                "[adventure.chest] player={} region={} lootTable={} pos={} alpha={} base={} cw={} pw={} hp={} allowance={} amount={} deposited={}",
                player.scoreboardName,
                location.region.name,
                container.lootTable,
                container.blockPos,
                alpha,
                baseScore,
                classWeight,
                phaseWeight,
                heatPenalty,
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

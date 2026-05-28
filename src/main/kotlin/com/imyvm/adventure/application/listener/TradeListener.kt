package com.imyvm.adventure.application.listener

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.api.event.TradeEvents
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.domain.model.ActionClass
import com.imyvm.adventure.domain.model.ActionEventType
import com.imyvm.adventure.infra.config.EconomyConfig
import net.minecraft.core.registries.BuiltInRegistries

class TradeListener {
    fun register() {
        TradeEvents.ON_TRADE_COMPLETED.register { player, result, merchant ->
            val location = AdventureServices.scopeResolver.resolveAdventureLocation(player)
            if (location == null) {
                WorldGeoAdventureAddon.logger.debug(
                    "[adventure.trade] player={} outside adventure scope",
                    player.scoreboardName
                )
                return@register
            }

            val eventType = ActionEventType.TRADE
            val actionClass = ActionClass.LOGISTICS_TRADE
            val alpha = EconomyConfig.allowanceFractionFor(actionClass)
            val baseScore = EconomyConfig.baseScoreFor(eventType)
            val classWeight = EconomyConfig.classWeightFor(actionClass)
            val phaseWeight = MoonPhase.currentWeight()
            val heatPenalty = HEAT_PENALTY_PLACEHOLDER
            val allowance = alpha * baseScore * classWeight * phaseWeight * (1.0 - heatPenalty)
            val amount = (allowance * 100.0).toLong()
            val deposited = AdventureServices.economyBridgeService.deposit(player, amount)

            WorldGeoAdventureAddon.logger.info(
                "[adventure.trade] player={} region={} item={} count={} merchant={} alpha={} base={} cw={} pw={} hp={} allowance={} amount={} deposited={}",
                player.scoreboardName,
                location.region.name,
                BuiltInRegistries.ITEM.getKey(result.item),
                result.count,
                merchant::class.java.simpleName,
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

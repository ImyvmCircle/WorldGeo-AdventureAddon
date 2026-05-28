package com.imyvm.adventure.application.listener

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.api.event.ProbeEvents
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.domain.model.ActionClass
import com.imyvm.adventure.domain.model.ActionEventType
import com.imyvm.adventure.infra.config.EconomyConfig
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Items

class ProbeUseListener {
    fun register() {
        UseItemCallback.EVENT.register { player, _, hand ->
            val stack = player.getItemInHand(hand)
            if (stack.`is`(Items.SPYGLASS) && player is ServerPlayer) {
                ProbeEvents.ON_SPYGLASS_USED.invoker().onSpyglassUsed(player)
            }
            InteractionResult.PASS
        }

        ProbeEvents.ON_SPYGLASS_USED.register { player ->
            val location = AdventureServices.scopeResolver.resolveAdventureLocation(player)
            if (location == null) {
                WorldGeoAdventureAddon.logger.debug(
                    "[adventure.probe] player={} outside adventure scope",
                    player.scoreboardName
                )
                return@register
            }

            val eventType = ActionEventType.READ
            val actionClass = ActionClass.PROBE
            val alpha = EconomyConfig.allowanceFractionFor(actionClass)
            val baseScore = EconomyConfig.baseScoreFor(eventType)
            val classWeight = EconomyConfig.classWeightFor(actionClass)
            val phaseWeight = MoonPhase.currentWeight()
            val heatPenalty = HEAT_PENALTY_PLACEHOLDER
            val allowance = alpha * baseScore * classWeight * phaseWeight * (1.0 - heatPenalty)
            val amount = (allowance * 100.0).toLong()
            val deposited = AdventureServices.economyBridgeService.deposit(player, amount)

            WorldGeoAdventureAddon.logger.info(
                "[adventure.probe] event={} player={} region={} alpha={} base={} cw={} pw={} hp={} allowance={} amount={} deposited={}",
                eventType.configKey,
                player.scoreboardName,
                location.region.name,
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

package com.imyvm.adventure.entrypoint.register.event

import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.infra.config.GameplayConfig
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult

fun registerAdventureEvents() {
    registerAnchorInteraction()
    registerAdventureScheduler()
}

private fun registerAnchorInteraction() {
    UseBlockCallback.EVENT.register(UseBlockCallback { player, level, hand, hitResult ->
        if (!AdventureServices.isReady() || level.isClientSide || player !is ServerPlayer) {
            return@UseBlockCallback InteractionResult.PASS
        }

        if (GameplayConfig.ANCHOR_MAIN_HAND_ONLY.value && hand != InteractionHand.MAIN_HAND) {
            return@UseBlockCallback InteractionResult.PASS
        }

        AdventureServices.anchorInteractionService.handle(
            player = player,
            level = level,
            blockPos = hitResult.blockPos
        )
    })
}

private fun registerAdventureScheduler() {
    ServerTickEvents.END_SERVER_TICK.register { server ->
        if (AdventureServices.isReady()) {
            AdventureServices.scheduleService.onServerTick(server)
        }
    }
}

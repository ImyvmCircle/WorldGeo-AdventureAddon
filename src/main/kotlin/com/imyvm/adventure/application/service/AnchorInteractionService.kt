package com.imyvm.adventure.application.service

import com.imyvm.adventure.util.text.Translator
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.level.Level

class AnchorInteractionService(
    private val registryService: AdventureRegistryService,
    private val worldGeoBridgeService: WorldGeoBridgeService
) {
    fun handle(player: ServerPlayer, level: Level, blockPos: BlockPos): InteractionResult {
        val anchor = registryService.findAnchor(level.dimension().toString(), blockPos)
            ?: return InteractionResult.PASS

        val resolved = worldGeoBridgeService.resolveAt(level, blockPos)
            ?: return InteractionResult.PASS

        if (anchor.regionNumberId != resolved.region.numberID) {
            player.sendSystemMessage(
                Translator.tr(
                    "interaction.anchor.region_mismatch",
                    anchor.title,
                    resolved.region.name
                )
            )
            return InteractionResult.FAIL
        }

        registryService.getOrCreateRegionProfile(
            regionNumberId = resolved.region.numberID,
            displayName = resolved.region.name
        )

        player.sendSystemMessage(
            Translator.tr(
                "interaction.anchor.open",
                anchor.title,
                resolved.region.name,
                resolved.scope?.scopeName ?: Translator.raw("label.none") ?: "none"
            )
        )
        return InteractionResult.SUCCESS
    }
}

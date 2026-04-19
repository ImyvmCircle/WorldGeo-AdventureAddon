package com.imyvm.adventure.application.interaction.command

import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.domain.model.AdventureAnchor
import com.imyvm.adventure.util.text.Translator
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

fun onInitRegionAtCurrentLocation(player: ServerPlayer): Int {
    val resolved = AdventureServices.worldGeoBridgeService.resolveForPlayer(player)
    if (resolved == null) {
        player.sendSystemMessage(Translator.tr("command.admin.region.init.error.no_region"))
        return 0
    }

    val profile = AdventureServices.registryService.getOrCreateRegionProfile(
        regionNumberId = resolved.region.numberID,
        displayName = resolved.region.name
    )
    player.sendSystemMessage(
        Translator.tr(
            "command.admin.region.init.success",
            profile.regionNumberId,
            profile.displayName
        )
    )
    return 1
}

fun onInitRegionById(player: ServerPlayer, regionNumberId: Int): Int {
    val region = AdventureServices.worldGeoBridgeService.getRegion(regionNumberId)
    if (region == null) {
        player.sendSystemMessage(Translator.tr("command.admin.region.init.error.not_found", regionNumberId))
        return 0
    }

    val profile = AdventureServices.registryService.getOrCreateRegionProfile(
        regionNumberId = region.numberID,
        displayName = region.name
    )
    player.sendSystemMessage(
        Translator.tr(
            "command.admin.region.init.success",
            profile.regionNumberId,
            profile.displayName
        )
    )
    return 1
}

fun onCreateAnchor(
    player: ServerPlayer,
    kind: String,
    title: String?,
    x: Int? = null,
    y: Int? = null,
    z: Int? = null
): Int {
    val blockPos = resolveCommandBlockPos(player, x, y, z)
    val level = player.level()
    val worldId = AdventureServices.worldGeoBridgeService.getWorldId(level)

    val existing = AdventureServices.registryService.findAnchor(worldId, blockPos, includeDisabled = true)
    if (existing != null) {
        player.sendSystemMessage(
            Translator.tr(
                "command.admin.anchor.create.error.exists",
                existing.title,
                blockPos.x,
                blockPos.y,
                blockPos.z
            )
        )
        return 0
    }

    val resolved = AdventureServices.worldGeoBridgeService.resolveAt(level, blockPos)
    if (resolved == null) {
        player.sendSystemMessage(
            Translator.tr(
                "command.admin.anchor.create.error.no_region",
                blockPos.x,
                blockPos.y,
                blockPos.z
            )
        )
        return 0
    }

    val anchor = AdventureAnchor(
        anchorId = "anchor-${UUID.randomUUID()}",
        regionNumberId = resolved.region.numberID,
        scopeName = resolved.scope?.scopeName,
        title = title?.ifBlank { kind } ?: kind,
        worldId = worldId,
        x = blockPos.x,
        y = blockPos.y,
        z = blockPos.z,
        kind = kind.uppercase()
    )

    AdventureServices.registryService.upsertAnchor(anchor)
    AdventureServices.registryService.getOrCreateRegionProfile(
        regionNumberId = resolved.region.numberID,
        displayName = resolved.region.name
    )

    player.sendSystemMessage(
        Translator.tr(
            "command.admin.anchor.create.success",
            anchor.kind,
            anchor.title,
            resolved.region.name,
            resolved.scope?.scopeName ?: Translator.raw("label.none") ?: "none",
            blockPos.x,
            blockPos.y,
            blockPos.z
        )
    )
    return 1
}

fun onAnchorInfo(player: ServerPlayer, x: Int? = null, y: Int? = null, z: Int? = null): Int {
    val blockPos = resolveCommandBlockPos(player, x, y, z)
    val worldId = AdventureServices.worldGeoBridgeService.getWorldId(player.level())
    val anchor = AdventureServices.registryService.findAnchor(worldId, blockPos, includeDisabled = true)
    if (anchor == null) {
        player.sendSystemMessage(
            Translator.tr(
                "command.admin.anchor.info.error.not_found",
                blockPos.x,
                blockPos.y,
                blockPos.z
            )
        )
        return 0
    }

    val profile = AdventureServices.registryService.getRegionProfile(anchor.regionNumberId)
    player.sendSystemMessage(
        Translator.tr(
            "command.admin.anchor.info.success",
            anchor.anchorId,
            anchor.kind,
            anchor.title,
            anchor.regionNumberId,
            profile?.displayName ?: Translator.raw("label.none") ?: "none",
            anchor.scopeName ?: Translator.raw("label.none") ?: "none",
            anchor.enabled,
            anchor.x,
            anchor.y,
            anchor.z
        )
    )
    return 1
}

fun onRemoveAnchor(player: ServerPlayer, x: Int? = null, y: Int? = null, z: Int? = null): Int {
    val blockPos = resolveCommandBlockPos(player, x, y, z)
    val worldId = AdventureServices.worldGeoBridgeService.getWorldId(player.level())
    val anchor = AdventureServices.registryService.findAnchor(worldId, blockPos, includeDisabled = true)
    if (anchor == null) {
        player.sendSystemMessage(
            Translator.tr(
                "command.admin.anchor.remove.error.not_found",
                blockPos.x,
                blockPos.y,
                blockPos.z
            )
        )
        return 0
    }

    AdventureServices.registryService.removeAnchor(anchor.anchorId)
    player.sendSystemMessage(
        Translator.tr(
            "command.admin.anchor.remove.success",
            anchor.kind,
            anchor.title,
            anchor.x,
            anchor.y,
            anchor.z
        )
    )
    return 1
}

private fun resolveCommandBlockPos(player: ServerPlayer, x: Int?, y: Int?, z: Int?): BlockPos =
    if (x != null && y != null && z != null) {
        BlockPos(x, y, z)
    } else {
        player.blockPosition()
    }

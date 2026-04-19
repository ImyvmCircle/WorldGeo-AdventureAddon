package com.imyvm.adventure.entrypoint.register.command

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.application.AdventureBootstrap
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.application.interaction.command.onAnchorInfo
import com.imyvm.adventure.application.interaction.command.onCreateAnchor
import com.imyvm.adventure.application.interaction.command.onInitRegionAtCurrentLocation
import com.imyvm.adventure.application.interaction.command.onInitRegionById
import com.imyvm.adventure.application.interaction.command.onRemoveAnchor
import com.imyvm.adventure.infra.AdventureDatabase
import com.imyvm.adventure.infra.config.AdventureConfig
import com.imyvm.adventure.infra.config.FinanceConfig
import com.imyvm.adventure.infra.config.GameplayConfig
import com.imyvm.adventure.util.text.Translator
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands as MinecraftCommands
import net.minecraft.commands.Commands.literal

fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
    dispatcher.register(
        literal("adventure")
            .executes { runAbout(it) }
            .then(
                literal("about")
                    .executes { runAbout(it) }
            )
            .then(
                literal("reload")
                    .requires(MinecraftCommands.hasPermission(MinecraftCommands.LEVEL_GAMEMASTERS))
                    .executes { runReload(it) }
            )
            .then(
                literal("debug")
                    .requires(MinecraftCommands.hasPermission(MinecraftCommands.LEVEL_GAMEMASTERS))
                    .then(
                        literal("context")
                            .executes { runDebugContext(it) }
                    )
            )
            .then(
                literal("admin")
                    .requires(MinecraftCommands.hasPermission(MinecraftCommands.LEVEL_GAMEMASTERS))
                    .then(
                        literal("region")
                            .then(
                                literal("init")
                                    .executes { runAdminRegionInitCurrent(it) }
                                    .then(
                                        argument("regionId", IntegerArgumentType.integer(1))
                                            .executes { runAdminRegionInitById(it) }
                                    )
                            )
                    )
                    .then(
                        literal("anchor")
                            .then(
                                literal("create")
                                    .then(
                                        argument("kind", StringArgumentType.word())
                                            .executes { runAdminAnchorCreate(it) }
                                            .then(
                                                argument("title", StringArgumentType.string())
                                                    .executes { runAdminAnchorCreate(it) }
                                                    .then(
                                                        argument("x", IntegerArgumentType.integer())
                                                            .then(
                                                                argument("y", IntegerArgumentType.integer())
                                                                    .then(
                                                                        argument("z", IntegerArgumentType.integer())
                                                                            .executes { runAdminAnchorCreate(it) }
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                            .then(
                                literal("info")
                                    .executes { runAdminAnchorInfo(it) }
                                    .then(
                                        argument("x", IntegerArgumentType.integer())
                                            .then(
                                                argument("y", IntegerArgumentType.integer())
                                                    .then(
                                                        argument("z", IntegerArgumentType.integer())
                                                            .executes { runAdminAnchorInfo(it) }
                                                    )
                                            )
                                    )
                            )
                            .then(
                                literal("remove")
                                    .executes { runAdminAnchorRemove(it) }
                                    .then(
                                        argument("x", IntegerArgumentType.integer())
                                            .then(
                                                argument("y", IntegerArgumentType.integer())
                                                    .then(
                                                        argument("z", IntegerArgumentType.integer())
                                                            .executes { runAdminAnchorRemove(it) }
                                                    )
                                            )
                                    )
                            )
                    )
            )
    )
}

private fun runAbout(context: CommandContext<CommandSourceStack>): Int {
    val source = context.source
    val registryService = AdventureServices.registryService

    val lines = listOf(
        Translator.tr("command.about.header"),
        Translator.tr("command.about.version", AdventureBootstrap.currentVersion()),
        Translator.tr("command.about.language", AdventureConfig.LANGUAGE.value),
        Translator.tr(
            "command.about.database",
            AdventureDatabase.state.schemaVersion,
            registryService.countRegions(),
            registryService.countAnchors(),
            registryService.countProjects()
        ),
        Translator.tr(
            "command.about.cadence",
            GameplayConfig.BOARD_REFRESH_MINUTES.value,
            GameplayConfig.MARKET_SETTLEMENT_HOURS.value
        ),
        Translator.tr(
            "command.about.finance",
            FinanceConfig.SHARE_MAX_SETTLEMENT_TRADES.value,
            FinanceConfig.SHARE_MAX_TOTAL_EXPOSURE_PERCENT.value,
            FinanceConfig.SHARE_MAX_PROJECT_EXPOSURE_PERCENT.value
        )
    )

    lines.forEach { line ->
        source.sendSuccess({ line }, false)
    }
    return 1
}

private fun runReload(context: CommandContext<CommandSourceStack>): Int {
    AdventureBootstrap.reload()
    context.source.sendSuccess({ Translator.tr("command.reload.success") }, true)
    WorldGeoAdventureAddon.logger.info("Adventure configuration reloaded by {}", context.source.displayName.string)
    return 1
}

private fun runDebugContext(context: CommandContext<CommandSourceStack>): Int {
    context.source.sendSuccess(
        {
            Translator.tr(
                "command.debug.context",
                AdventureServices.isReady(),
                WorldGeoAdventureAddon.server != null
            )
        },
        false
    )
    return 1
}

private fun runAdminRegionInitCurrent(context: CommandContext<CommandSourceStack>): Int {
    val player = context.source.player ?: return 0
    return onInitRegionAtCurrentLocation(player)
}

private fun runAdminRegionInitById(context: CommandContext<CommandSourceStack>): Int {
    val player = context.source.player ?: return 0
    val regionId = IntegerArgumentType.getInteger(context, "regionId")
    return onInitRegionById(player, regionId)
}

private fun runAdminAnchorCreate(context: CommandContext<CommandSourceStack>): Int {
    val player = context.source.player ?: return 0
    val kind = StringArgumentType.getString(context, "kind")
    val title = runCatching { StringArgumentType.getString(context, "title") }.getOrNull()
    val x = runCatching { IntegerArgumentType.getInteger(context, "x") }.getOrNull()
    val y = runCatching { IntegerArgumentType.getInteger(context, "y") }.getOrNull()
    val z = runCatching { IntegerArgumentType.getInteger(context, "z") }.getOrNull()
    return onCreateAnchor(player, kind, title, x, y, z)
}

private fun runAdminAnchorInfo(context: CommandContext<CommandSourceStack>): Int {
    val player = context.source.player ?: return 0
    val x = runCatching { IntegerArgumentType.getInteger(context, "x") }.getOrNull()
    val y = runCatching { IntegerArgumentType.getInteger(context, "y") }.getOrNull()
    val z = runCatching { IntegerArgumentType.getInteger(context, "z") }.getOrNull()
    return onAnchorInfo(player, x, y, z)
}

private fun runAdminAnchorRemove(context: CommandContext<CommandSourceStack>): Int {
    val player = context.source.player ?: return 0
    val x = runCatching { IntegerArgumentType.getInteger(context, "x") }.getOrNull()
    val y = runCatching { IntegerArgumentType.getInteger(context, "y") }.getOrNull()
    val z = runCatching { IntegerArgumentType.getInteger(context, "z") }.getOrNull()
    return onRemoveAnchor(player, x, y, z)
}

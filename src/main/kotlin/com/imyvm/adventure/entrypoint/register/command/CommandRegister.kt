package com.imyvm.adventure.entrypoint.register.command

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.application.AdventureBootstrap
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.infra.AdventureDatabase
import com.imyvm.adventure.infra.config.AdventureConfig
import com.imyvm.adventure.infra.config.GameplayConfig
import com.imyvm.adventure.util.text.Translator
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
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
    )
}

private fun runAbout(context: CommandContext<CommandSourceStack>): Int {
    val source = context.source

    val lines = listOf(
        Translator.tr("command.about.header"),
        Translator.tr("command.about.version", AdventureBootstrap.currentVersion()),
        Translator.tr("command.about.language", AdventureConfig.LANGUAGE.value),
        Translator.tr(
            "command.about.database",
            AdventureDatabase.state.schemaVersion
        ),
        Translator.tr(
            "command.about.runtime",
            AdventureConfig.TIMEZONE.value,
            GameplayConfig.SCHEDULER_HEARTBEAT_SECONDS.value,
            AdventureConfig.DEBUG_LOGGING.value
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

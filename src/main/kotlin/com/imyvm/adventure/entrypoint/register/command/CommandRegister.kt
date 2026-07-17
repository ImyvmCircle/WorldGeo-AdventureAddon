package com.imyvm.adventure.entrypoint.register.command

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.application.AdventureBootstrap
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.entrypoint.api.WildernessApi
import com.imyvm.adventure.infra.AdventureDatabase
import com.imyvm.adventure.infra.WildernessConfig
import com.imyvm.adventure.util.text.Translator
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal

private const val WILDERNESSES_PER_PAGE = 8

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
                    .requires { net.minecraft.commands.Commands.LEVEL_GAMEMASTERS.check(it.permissions()) }
                    .executes { runReload(it) }
            )
    )

    dispatcher.register(
        literal("wilderness")
            .executes { runWildernessHelp(it) }
            .then(
                literal("create")
                    .requires { net.minecraft.commands.Commands.LEVEL_GAMEMASTERS.check(it.permissions()) }
                    .then(
                        argument("regionNumberId", IntegerArgumentType.integer(1))
                            .then(
                                argument("name", StringArgumentType.greedyString())
                                    .executes { runWildernessCreate(it) }
                            )
                    )
            )
            .then(
                literal("delete")
                    .requires { net.minecraft.commands.Commands.LEVEL_GAMEMASTERS.check(it.permissions()) }
                    .then(
                        argument("regionNumberId", IntegerArgumentType.integer(1))
                            .executes { runWildernessDelete(it) }
                    )
            )
            .then(
                literal("info")
                    .then(
                        argument("regionNumberId", IntegerArgumentType.integer(1))
                            .executes { runWildernessInfo(it) }
                    )
            )
            .then(
                literal("list")
                    .executes { runWildernessList(it, 1) }
                    .then(
                        argument("page", IntegerArgumentType.integer(1))
                            .executes { runWildernessList(it, IntegerArgumentType.getInteger(it, "page")) }
                    )
            )
    )
}

private fun runAbout(context: CommandContext<CommandSourceStack>): Int {
    val source = context.source

    val lines = listOf(
        Translator.tr("command.about.header"),
        Translator.tr("command.about.version", AdventureBootstrap.currentVersion()),
        Translator.tr("command.about.language", WildernessConfig.LANGUAGE.value),
        Translator.tr("command.about.database", AdventureDatabase.schemaVersion)
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

private fun runWildernessHelp(context: CommandContext<CommandSourceStack>): Int {
    context.source.sendSuccess({ Translator.tr("command.wilderness.help") }, false)
    return 1
}

private fun runWildernessCreate(context: CommandContext<CommandSourceStack>): Int {
    val source = context.source
    val regionNumberId = IntegerArgumentType.getInteger(context, "regionNumberId")
    val name = StringArgumentType.getString(context, "name")

    if (!AdventureServices.isReady()) {
        source.sendFailure(Translator.tr("command.wilderness.error.not_ready"))
        return 0
    }

    return WildernessApi.create(regionNumberId, name).fold(
        onSuccess = { snapshot ->
            source.sendSuccess(
                { Translator.tr("command.wilderness.create.success", snapshot.regionNumberId, snapshot.name) },
                true
            )
            1
        },
        onFailure = { error ->
            source.sendFailure(Translator.tr("command.wilderness.create.failure", regionNumberId, error.message ?: "unknown"))
            0
        }
    )
}

private fun runWildernessDelete(context: CommandContext<CommandSourceStack>): Int {
    val source = context.source
    val regionNumberId = IntegerArgumentType.getInteger(context, "regionNumberId")

    if (!AdventureServices.isReady()) {
        source.sendFailure(Translator.tr("command.wilderness.error.not_ready"))
        return 0
    }

    return WildernessApi.delete(regionNumberId).fold(
        onSuccess = {
            source.sendSuccess(
                { Translator.tr("command.wilderness.delete.success", regionNumberId) },
                true
            )
            1
        },
        onFailure = { error ->
            source.sendFailure(
                Translator.tr(
                    "command.wilderness.delete.failure",
                    regionNumberId,
                    error.message ?: "unknown"
                )
            )
            0
        }
    )
}

private fun runWildernessInfo(context: CommandContext<CommandSourceStack>): Int {
    val source = context.source
    val regionNumberId = IntegerArgumentType.getInteger(context, "regionNumberId")

    val snapshot = WildernessApi.getByRegion(regionNumberId)
    if (snapshot == null) {
        source.sendFailure(Translator.tr("command.wilderness.info.not_found", regionNumberId))
        return 0
    }

    source.sendSuccess(
        { Translator.tr("command.wilderness.info.header", snapshot.regionNumberId, snapshot.name) },
        false
    )
    source.sendSuccess(
        { Translator.tr("command.wilderness.info.status", snapshot.status.name) },
        false
    )
    source.sendSuccess(
        { Translator.tr("command.wilderness.info.created", snapshot.creationTime) },
        false
    )
    return 1
}

private fun runWildernessList(context: CommandContext<CommandSourceStack>, page: Int): Int {
    val source = context.source
    val all = WildernessApi.list()

    if (all.isEmpty()) {
        source.sendSuccess({ Translator.tr("command.wilderness.list.empty") }, false)
        return 1
    }

    val totalPages = (all.size + WILDERNESSES_PER_PAGE - 1) / WILDERNESSES_PER_PAGE
    val effectivePage = page.coerceIn(1, totalPages)
    val startIndex = (effectivePage - 1) * WILDERNESSES_PER_PAGE
    val pageItems = all.drop(startIndex).take(WILDERNESSES_PER_PAGE)

    source.sendSuccess(
        { Translator.tr("command.wilderness.list.header", effectivePage, totalPages, all.size) },
        false
    )
    pageItems.forEach { snapshot ->
        source.sendSuccess(
            { Translator.tr("command.wilderness.list.entry", snapshot.regionNumberId, snapshot.name, snapshot.status.name) },
            false
        )
    }
    return 1
}

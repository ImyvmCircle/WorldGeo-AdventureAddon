package com.imyvm.adventure

import com.imyvm.adventure.application.AdventureBootstrap
import com.imyvm.adventure.entrypoint.register.event.registerAdventureEvents
import com.imyvm.adventure.entrypoint.register.command.register
import com.imyvm.adventure.entrypoint.register.registerDataLoadAndSave
import com.imyvm.adventure.util.text.Translator
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.minecraft.server.MinecraftServer

class WorldGeoAdventureAddon : ModInitializer {

    override fun onInitialize() {
        registerDataLoadAndSave()
        AdventureBootstrap.initialize()
        registerAdventureEvents()
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> register(dispatcher) }

        logger.info(
            Translator.raw("system.lifecycle.initialized", AdventureBootstrap.currentVersion())
                ?: "$MOD_ID initialized successfully."
        )
    }

    companion object {
        const val MOD_ID = "adventure"
        val logger: Logger = LoggerFactory.getLogger(MOD_ID)
        var server: MinecraftServer? = null

        fun currentVersion(): String = FabricLoader.getInstance()
            .getModContainer(MOD_ID)
            .map { it.metadata.version.friendlyString }
            .orElse("unknown")
    }
}

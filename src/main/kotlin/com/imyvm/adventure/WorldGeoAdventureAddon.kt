package com.imyvm.adventure

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WorldGeoAdventureAddon : ModInitializer {

    override fun onInitialize() {
        logger.info("$MOD_ID initialized.")
    }

    companion object {
        const val MOD_ID = "adventure"
        val logger: Logger = LoggerFactory.getLogger(MOD_ID)
    }
}

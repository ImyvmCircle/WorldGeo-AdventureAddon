package com.imyvm.adventure.entrypoint.register

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.infra.AdventureDatabase
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

fun registerDataLoadAndSave() {
    dataLoad()
    dataSave()
    captureServerInstance()
}

private fun dataLoad() {
    try {
        AdventureDatabase.load()
    } catch (e: Exception) {
        WorldGeoAdventureAddon.logger.error("Failed to load adventure database: ${e.message}", e)
    }
}

private fun dataSave() {
    ServerLifecycleEvents.SERVER_STOPPING.register { _ ->
        try {
            AdventureDatabase.save()
        } catch (e: Exception) {
            WorldGeoAdventureAddon.logger.error("Failed to save adventure database: ${e.message}", e)
        }
    }
}

private fun captureServerInstance() {
    ServerLifecycleEvents.SERVER_STARTED.register { server ->
        WorldGeoAdventureAddon.server = server
    }

    ServerLifecycleEvents.SERVER_STOPPING.register { _ ->
        WorldGeoAdventureAddon.server = null
    }
}

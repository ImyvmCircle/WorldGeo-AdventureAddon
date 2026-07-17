package com.imyvm.adventure.entrypoint.register

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.infra.AdventureDatabase
import com.imyvm.adventure.infra.WildernessDatabase
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

fun registerDataLoadAndSave() {
    captureServerInstance()
    dataLoad()
    dataSave()
}

private fun dataLoad() {
    ServerLifecycleEvents.SERVER_STARTING.register { server ->
        try {
            WildernessDatabase.load(server)
        } catch (e: Exception) {
            WildernessDatabase.backupDatabaseAfterLoadFailure()
            WorldGeoAdventureAddon.logger.error("Failed to load wilderness database: ${e.message}", e)
            throw IllegalStateException("Wilderness database corrupt; server startup aborted", e)
        }
        try {
            AdventureDatabase.load(server)
        } catch (e: Exception) {
            WorldGeoAdventureAddon.logger.error("Failed to load adventure database: ${e.message}", e)
        }
    }
}

private fun dataSave() {
    ServerLifecycleEvents.SERVER_STOPPING.register { server ->
        try {
            WildernessDatabase.save()
        } catch (e: Exception) {
            WorldGeoAdventureAddon.logger.error("Failed to save wilderness database: ${e.message}", e)
        }
        try {
            AdventureDatabase.save(server)
        } catch (e: Exception) {
            WorldGeoAdventureAddon.logger.error("Failed to save adventure database: ${e.message}", e)
        }
    }
}

private fun captureServerInstance() {
    ServerLifecycleEvents.SERVER_STARTING.register { server ->
        WorldGeoAdventureAddon.server = server
    }

    ServerLifecycleEvents.SERVER_STOPPING.register { _ ->
        WorldGeoAdventureAddon.server = null
    }
}

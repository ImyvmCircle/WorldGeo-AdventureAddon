package com.imyvm.adventure.entrypoint.register.event

import com.imyvm.adventure.application.AdventureServices
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents

fun registerAdventureEvents() {
    registerAdventureScheduler()
}

private fun registerAdventureScheduler() {
    ServerTickEvents.END_SERVER_TICK.register { server ->
        if (AdventureServices.isReady()) {
            AdventureServices.scheduleService.onServerTick(server)
        }
    }
}

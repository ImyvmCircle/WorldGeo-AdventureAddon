package com.imyvm.adventure.entrypoint.register.event

import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.application.listener.CombatListener
import com.imyvm.adventure.application.listener.ContainerListener
import com.imyvm.adventure.application.listener.SampleBlockListener
import com.imyvm.adventure.application.listener.TradeListener
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents

fun registerAdventureEvents() {
    registerAdventureScheduler()
    TradeListener().register()
    ContainerListener().register()
    CombatListener().register()
    SampleBlockListener().register()
}

private fun registerAdventureScheduler() {
    ServerTickEvents.END_SERVER_TICK.register { server ->
        if (AdventureServices.isReady()) {
            AdventureServices.scheduleService.onServerTick(server)
        }
    }
}

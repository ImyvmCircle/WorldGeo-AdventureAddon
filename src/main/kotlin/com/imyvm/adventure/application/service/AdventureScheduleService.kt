package com.imyvm.adventure.application.service

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.infra.config.AdventureConfig
import com.imyvm.adventure.infra.config.GameplayConfig
import net.minecraft.server.MinecraftServer

class AdventureScheduleService {
    private var totalTicks: Long = 0

    fun onServerTick(server: MinecraftServer) {
        totalTicks++

        if (!AdventureConfig.DEBUG_LOGGING.value) {
            return
        }

        val heartbeatSeconds = GameplayConfig.SCHEDULER_HEARTBEAT_SECONDS.value.coerceAtLeast(1)
        if (totalTicks % (heartbeatSeconds * 20L) == 0L) {
            WorldGeoAdventureAddon.logger.debug("Adventure scheduler heartbeat, tick={}", totalTicks)
        }
    }

    fun totalTicks(): Long = totalTicks
}

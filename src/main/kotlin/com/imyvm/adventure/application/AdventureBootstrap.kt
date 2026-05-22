package com.imyvm.adventure.application

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.application.service.AdventureScheduleService
import com.imyvm.adventure.application.service.WorldGeoBridgeService
import com.imyvm.adventure.infra.config.AdventureConfig
import com.imyvm.adventure.infra.config.GameplayConfig

object AdventureBootstrap {
    private val adventureConfig = AdventureConfig()
    private val gameplayConfig = GameplayConfig()

    fun initialize() {
        loadConfigs()
        bindServices()
    }

    fun reload() {
        loadConfigs()
    }

    fun currentVersion(): String = WorldGeoAdventureAddon.currentVersion()

    private fun loadConfigs() {
        adventureConfig.loadAndSave()
        gameplayConfig.loadAndSave()
    }

    private fun bindServices() {
        val worldGeoBridgeService = WorldGeoBridgeService()
        val scheduleService = AdventureScheduleService()

        AdventureServices.bind(
            worldGeoBridgeService = worldGeoBridgeService,
            scheduleService = scheduleService
        )
    }
}

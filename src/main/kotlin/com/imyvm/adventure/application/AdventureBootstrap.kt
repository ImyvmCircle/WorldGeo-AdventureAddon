package com.imyvm.adventure.application

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.application.service.AdventureEconomyService
import com.imyvm.adventure.application.service.AdventureRegistryService
import com.imyvm.adventure.application.service.AdventureScheduleService
import com.imyvm.adventure.application.service.AnchorInteractionService
import com.imyvm.adventure.application.service.WorldGeoBridgeService
import com.imyvm.adventure.infra.config.AdventureConfig
import com.imyvm.adventure.infra.config.FinanceConfig
import com.imyvm.adventure.infra.config.GameplayConfig

object AdventureBootstrap {
    private val adventureConfig = AdventureConfig()
    private val gameplayConfig = GameplayConfig()
    private val financeConfig = FinanceConfig()

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
        financeConfig.loadAndSave()
    }

    private fun bindServices() {
        val registryService = AdventureRegistryService()
        val worldGeoBridgeService = WorldGeoBridgeService()
        val economyService = AdventureEconomyService()
        val scheduleService = AdventureScheduleService()
        val anchorInteractionService = AnchorInteractionService(
            registryService = registryService,
            worldGeoBridgeService = worldGeoBridgeService
        )

        AdventureServices.bind(
            registryService = registryService,
            worldGeoBridgeService = worldGeoBridgeService,
            economyService = economyService,
            scheduleService = scheduleService,
            anchorInteractionService = anchorInteractionService
        )
    }
}

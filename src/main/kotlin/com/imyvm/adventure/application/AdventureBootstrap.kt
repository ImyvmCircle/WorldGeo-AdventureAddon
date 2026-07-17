package com.imyvm.adventure.application

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.application.service.CommunityBridgeService
import com.imyvm.adventure.application.service.WorldGeoBridgeService
import com.imyvm.adventure.infra.WildernessConfig

object AdventureBootstrap {
    private val wildernessConfig = WildernessConfig()

    fun initialize() {
        loadConfigs()
        bindServices()
    }

    fun reload() {
        loadConfigs()
    }

    fun currentVersion(): String = WorldGeoAdventureAddon.currentVersion()

    private fun loadConfigs() {
        wildernessConfig.loadAndSave()
        WildernessConfig.validateValues()
    }

    private fun bindServices() {
        val worldGeoBridgeService = WorldGeoBridgeService()
        val communityBridgeService = CommunityBridgeService()

        AdventureServices.bind(
            wildernessConfig = wildernessConfig,
            worldGeoBridgeService = worldGeoBridgeService,
            communityBridgeService = communityBridgeService
        )
    }
}

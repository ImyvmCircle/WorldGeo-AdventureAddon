package com.imyvm.adventure.application

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.application.service.AdventureScheduleService
import com.imyvm.adventure.application.service.CommunityBridgeService
import com.imyvm.adventure.application.service.ScopeResolver
import com.imyvm.adventure.application.service.WorldGeoBridgeService
import com.imyvm.adventure.entrypoint.data.ItemBasketLoader
import com.imyvm.adventure.entrypoint.data.LootWindowsLoader
import com.imyvm.adventure.entrypoint.data.ProbeTiersLoader
import com.imyvm.adventure.entrypoint.data.SampleWhitelistLoader
import com.imyvm.adventure.entrypoint.data.ScopeOverlaysLoader
import com.imyvm.adventure.infra.config.AdventureConfig
import com.imyvm.adventure.infra.config.EconomyConfig
import com.imyvm.adventure.infra.config.GameplayConfig
import com.imyvm.adventure.infra.config.IndicesConfig
import com.imyvm.adventure.infra.config.InsuranceConfig
import com.imyvm.adventure.infra.config.ResearchConfig
import com.imyvm.adventure.infra.config.SettlementConfig

object AdventureBootstrap {
    private val adventureConfig = AdventureConfig()
    private val gameplayConfig = GameplayConfig()
    private val economyConfig = EconomyConfig()
    private val indicesConfig = IndicesConfig()
    private val researchConfig = ResearchConfig()
    private val insuranceConfig = InsuranceConfig()
    private val settlementConfig = SettlementConfig()

    private val itemBasketLoader = ItemBasketLoader()
    private val lootWindowsLoader = LootWindowsLoader()
    private val probeTiersLoader = ProbeTiersLoader()
    private val sampleWhitelistLoader = SampleWhitelistLoader()
    private val scopeOverlaysLoader = ScopeOverlaysLoader()

    fun initialize() {
        loadConfigs()
        loadResources()
        bindServices()
    }

    fun reload() {
        loadConfigs()
        loadResources()
    }

    fun currentVersion(): String = WorldGeoAdventureAddon.currentVersion()

    private fun loadConfigs() {
        adventureConfig.loadAndSave()
        gameplayConfig.loadAndSave()
        economyConfig.loadAndSave()
        indicesConfig.loadAndSave()
        researchConfig.loadAndSave()
        insuranceConfig.loadAndSave()
        settlementConfig.loadAndSave()
    }

    private fun loadResources() {
        itemBasketLoader.load()
        lootWindowsLoader.load()
        probeTiersLoader.load()
        sampleWhitelistLoader.load()
        scopeOverlaysLoader.load()
    }

    private fun bindServices() {
        val worldGeoBridgeService = WorldGeoBridgeService()
        val communityBridgeService = CommunityBridgeService()
        val scheduleService = AdventureScheduleService()
        val scopeResolver = ScopeResolver(worldGeoBridgeService)

        AdventureServices.bind(
            worldGeoBridgeService = worldGeoBridgeService,
            communityBridgeService = communityBridgeService,
            scheduleService = scheduleService,
            scopeResolver = scopeResolver,
            itemBasketLoader = itemBasketLoader,
            lootWindowsLoader = lootWindowsLoader,
            probeTiersLoader = probeTiersLoader,
            sampleWhitelistLoader = sampleWhitelistLoader,
            scopeOverlaysLoader = scopeOverlaysLoader
        )
    }
}

package com.imyvm.adventure.application

import com.imyvm.adventure.application.service.AdventureScheduleService
import com.imyvm.adventure.application.service.CommunityBridgeService
import com.imyvm.adventure.application.service.EconomyBridgeService
import com.imyvm.adventure.application.service.RareCacheService
import com.imyvm.adventure.application.service.ScopeResolver
import com.imyvm.adventure.application.service.SessionManager
import com.imyvm.adventure.application.service.WorldGeoBridgeService
import com.imyvm.adventure.entrypoint.data.ItemBasketLoader
import com.imyvm.adventure.entrypoint.data.LootWindowsLoader
import com.imyvm.adventure.entrypoint.data.ProbeTiersLoader
import com.imyvm.adventure.entrypoint.data.SampleWhitelistLoader
import com.imyvm.adventure.entrypoint.data.ScopeOverlaysLoader

object AdventureServices {
    lateinit var worldGeoBridgeService: WorldGeoBridgeService
        private set
    lateinit var communityBridgeService: CommunityBridgeService
        private set
    lateinit var economyBridgeService: EconomyBridgeService
        private set
    lateinit var rareCacheService: RareCacheService
        private set
    lateinit var scheduleService: AdventureScheduleService
        private set
    lateinit var scopeResolver: ScopeResolver
        private set
    lateinit var sessionManager: SessionManager
        private set
    lateinit var itemBasketLoader: ItemBasketLoader
        private set
    lateinit var lootWindowsLoader: LootWindowsLoader
        private set
    lateinit var probeTiersLoader: ProbeTiersLoader
        private set
    lateinit var sampleWhitelistLoader: SampleWhitelistLoader
        private set
    lateinit var scopeOverlaysLoader: ScopeOverlaysLoader
        private set

    private var ready = false

    fun bind(
        worldGeoBridgeService: WorldGeoBridgeService,
        communityBridgeService: CommunityBridgeService,
        economyBridgeService: EconomyBridgeService,
        scheduleService: AdventureScheduleService,
        scopeResolver: ScopeResolver,
        sessionManager: SessionManager,
        rareCacheService: RareCacheService,
        itemBasketLoader: ItemBasketLoader,
        lootWindowsLoader: LootWindowsLoader,
        probeTiersLoader: ProbeTiersLoader,
        sampleWhitelistLoader: SampleWhitelistLoader,
        scopeOverlaysLoader: ScopeOverlaysLoader
    ) {
        this.worldGeoBridgeService = worldGeoBridgeService
        this.communityBridgeService = communityBridgeService
        this.economyBridgeService = economyBridgeService
        this.scheduleService = scheduleService
        this.scopeResolver = scopeResolver
        this.sessionManager = sessionManager
        this.rareCacheService = rareCacheService
        this.itemBasketLoader = itemBasketLoader
        this.lootWindowsLoader = lootWindowsLoader
        this.probeTiersLoader = probeTiersLoader
        this.sampleWhitelistLoader = sampleWhitelistLoader
        this.scopeOverlaysLoader = scopeOverlaysLoader
        ready = true
    }

    fun isReady(): Boolean = ready
}

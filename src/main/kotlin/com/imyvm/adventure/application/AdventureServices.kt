package com.imyvm.adventure.application

import com.imyvm.adventure.application.service.AdventureScheduleService
import com.imyvm.adventure.application.service.CommunityBridgeService
import com.imyvm.adventure.application.service.ScopeResolver
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
    lateinit var scheduleService: AdventureScheduleService
        private set
    lateinit var scopeResolver: ScopeResolver
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
        scheduleService: AdventureScheduleService,
        scopeResolver: ScopeResolver,
        itemBasketLoader: ItemBasketLoader,
        lootWindowsLoader: LootWindowsLoader,
        probeTiersLoader: ProbeTiersLoader,
        sampleWhitelistLoader: SampleWhitelistLoader,
        scopeOverlaysLoader: ScopeOverlaysLoader
    ) {
        this.worldGeoBridgeService = worldGeoBridgeService
        this.communityBridgeService = communityBridgeService
        this.scheduleService = scheduleService
        this.scopeResolver = scopeResolver
        this.itemBasketLoader = itemBasketLoader
        this.lootWindowsLoader = lootWindowsLoader
        this.probeTiersLoader = probeTiersLoader
        this.sampleWhitelistLoader = sampleWhitelistLoader
        this.scopeOverlaysLoader = scopeOverlaysLoader
        ready = true
    }

    fun isReady(): Boolean = ready
}

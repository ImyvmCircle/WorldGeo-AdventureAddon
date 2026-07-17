package com.imyvm.adventure.application

import com.imyvm.adventure.application.service.CommunityBridgeService
import com.imyvm.adventure.application.service.WorldGeoBridgeService
import com.imyvm.adventure.infra.WildernessConfig

object AdventureServices {
    lateinit var wildernessConfig: WildernessConfig
        private set
    lateinit var worldGeoBridgeService: WorldGeoBridgeService
        private set
    lateinit var communityBridgeService: CommunityBridgeService
        private set

    private var ready = false

    fun bind(
        wildernessConfig: WildernessConfig,
        worldGeoBridgeService: WorldGeoBridgeService,
        communityBridgeService: CommunityBridgeService
    ) {
        this.wildernessConfig = wildernessConfig
        this.worldGeoBridgeService = worldGeoBridgeService
        this.communityBridgeService = communityBridgeService
        ready = true
    }

    fun isReady(): Boolean = ready
}

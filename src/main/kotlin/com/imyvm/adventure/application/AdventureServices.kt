package com.imyvm.adventure.application

import com.imyvm.adventure.application.service.AdventureScheduleService
import com.imyvm.adventure.application.service.CommunityBridgeService
import com.imyvm.adventure.application.service.ScopeResolver
import com.imyvm.adventure.application.service.WorldGeoBridgeService

object AdventureServices {
    lateinit var worldGeoBridgeService: WorldGeoBridgeService
        private set
    lateinit var communityBridgeService: CommunityBridgeService
        private set
    lateinit var scheduleService: AdventureScheduleService
        private set
    lateinit var scopeResolver: ScopeResolver
        private set

    private var ready = false

    fun bind(
        worldGeoBridgeService: WorldGeoBridgeService,
        communityBridgeService: CommunityBridgeService,
        scheduleService: AdventureScheduleService,
        scopeResolver: ScopeResolver
    ) {
        this.worldGeoBridgeService = worldGeoBridgeService
        this.communityBridgeService = communityBridgeService
        this.scheduleService = scheduleService
        this.scopeResolver = scopeResolver
        ready = true
    }

    fun isReady(): Boolean = ready
}

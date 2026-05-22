package com.imyvm.adventure.application

import com.imyvm.adventure.application.service.AdventureScheduleService
import com.imyvm.adventure.application.service.WorldGeoBridgeService

object AdventureServices {
    lateinit var worldGeoBridgeService: WorldGeoBridgeService
        private set
    lateinit var scheduleService: AdventureScheduleService
        private set

    private var ready = false

    fun bind(
        worldGeoBridgeService: WorldGeoBridgeService,
        scheduleService: AdventureScheduleService
    ) {
        this.worldGeoBridgeService = worldGeoBridgeService
        this.scheduleService = scheduleService
        ready = true
    }

    fun isReady(): Boolean = ready
}

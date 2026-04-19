package com.imyvm.adventure.application

import com.imyvm.adventure.application.service.AdventureEconomyService
import com.imyvm.adventure.application.service.AdventureRegistryService
import com.imyvm.adventure.application.service.AdventureScheduleService
import com.imyvm.adventure.application.service.AnchorInteractionService
import com.imyvm.adventure.application.service.WorldGeoBridgeService

object AdventureServices {
    lateinit var registryService: AdventureRegistryService
        private set
    lateinit var worldGeoBridgeService: WorldGeoBridgeService
        private set
    lateinit var economyService: AdventureEconomyService
        private set
    lateinit var scheduleService: AdventureScheduleService
        private set
    lateinit var anchorInteractionService: AnchorInteractionService
        private set

    private var ready = false

    fun bind(
        registryService: AdventureRegistryService,
        worldGeoBridgeService: WorldGeoBridgeService,
        economyService: AdventureEconomyService,
        scheduleService: AdventureScheduleService,
        anchorInteractionService: AnchorInteractionService
    ) {
        this.registryService = registryService
        this.worldGeoBridgeService = worldGeoBridgeService
        this.economyService = economyService
        this.scheduleService = scheduleService
        this.anchorInteractionService = anchorInteractionService
        ready = true
    }

    fun isReady(): Boolean = ready
}

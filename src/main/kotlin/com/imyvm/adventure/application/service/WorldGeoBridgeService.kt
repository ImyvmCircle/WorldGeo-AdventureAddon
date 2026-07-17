package com.imyvm.adventure.application.service

import com.imyvm.iwg.inter.api.RegionDataApi

class WorldGeoBridgeService {
    fun regionExists(regionNumberId: Int): Boolean =
        RegionDataApi.getRegion(regionNumberId) != null
}

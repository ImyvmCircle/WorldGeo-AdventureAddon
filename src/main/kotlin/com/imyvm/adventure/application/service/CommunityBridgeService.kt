package com.imyvm.adventure.application.service

import com.imyvm.community.entrypoint.api.CommunityApi

class CommunityBridgeService {
    fun isCommunityRegion(regionNumberId: Int): Boolean =
        CommunityApi.getCommunityByRegion(regionNumberId) != null
}

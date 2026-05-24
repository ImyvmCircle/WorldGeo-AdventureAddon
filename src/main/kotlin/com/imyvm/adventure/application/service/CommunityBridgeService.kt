package com.imyvm.adventure.application.service

import com.imyvm.community.domain.model.Community
import com.imyvm.community.domain.model.TurnoverSource
import com.imyvm.community.domain.model.development.DevelopmentSnapshot
import com.imyvm.community.entrypoint.api.CommunityApi

class CommunityBridgeService {
    fun getCommunityByRegion(regionNumberId: Int): Community? =
        CommunityApi.getCommunityByRegion(regionNumberId)

    fun listCommunities(): List<Community> =
        CommunityApi.listCommunities()

    fun snapshotDevelopment(regionNumberId: Int, tick: Long): DevelopmentSnapshot? =
        CommunityApi.snapshotDevelopment(regionNumberId, tick)

    fun deposit(
        regionNumberId: Int,
        amount: Long,
        source: TurnoverSource,
        descriptionKey: String? = null,
        descriptionArgs: List<String> = emptyList()
    ): Result<Unit> = CommunityApi.deposit(regionNumberId, amount, source, descriptionKey, descriptionArgs)

    fun withdraw(
        regionNumberId: Int,
        amount: Long,
        source: TurnoverSource,
        descriptionKey: String? = null,
        descriptionArgs: List<String> = emptyList()
    ): Result<Unit> = CommunityApi.withdraw(regionNumberId, amount, source, descriptionKey, descriptionArgs)
}

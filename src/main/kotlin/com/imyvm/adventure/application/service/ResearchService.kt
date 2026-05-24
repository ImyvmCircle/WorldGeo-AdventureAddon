package com.imyvm.adventure.application.service

import com.imyvm.adventure.domain.model.ResearchFunding
import com.imyvm.adventure.domain.model.ResearchProgress
import java.util.UUID

class ResearchService {
    fun fund(sponsorUuid: UUID, scopeId: Long, amount: Long): ResearchFunding? = null
    fun submit(playerUuid: UUID, fundingId: Long, quality: Double): ResearchProgress? = null
    fun processMilestones(cycleId: Long) {}
}

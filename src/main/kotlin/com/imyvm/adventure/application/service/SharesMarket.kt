package com.imyvm.adventure.application.service

import com.imyvm.adventure.domain.model.IndexPosition
import java.util.UUID

class SharesMarket {
    fun buy(playerUuid: UUID, scopeId: Long, indexType: String, shares: Long): IndexPosition? = null
    fun sell(playerUuid: UUID, positionId: Long, shares: Long): Long = 0L
    fun settleAll(cycleId: Long) {}
}

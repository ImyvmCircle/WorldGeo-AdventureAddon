package com.imyvm.adventure.domain.model

import java.util.UUID

data class IndexPosition(
    val id: Long,
    val playerUuid: UUID,
    val scopeId: Long,
    val indexType: String,
    val shares: Long,
    val averagePrice: Double
)

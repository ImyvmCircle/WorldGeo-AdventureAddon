package com.imyvm.adventure.domain.model

import java.util.UUID

data class EconomyFlow(
    val id: Long,
    val cycleId: Long,
    val channel: String,
    val playerUuid: UUID?,
    val scopeId: Long?,
    val amount: Long,
    val tick: Long
)

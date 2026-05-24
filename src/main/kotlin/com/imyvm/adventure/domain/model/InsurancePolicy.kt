package com.imyvm.adventure.domain.model

import java.util.UUID

data class InsurancePolicy(
    val id: Long,
    val playerUuid: UUID,
    val scopeId: Long,
    val premium: Long,
    val coverage: Long,
    val openTick: Long,
    val expireTick: Long
)

package com.imyvm.adventure.domain.model

import java.util.UUID

data class OperationSession(
    val id: Long,
    val playerUuid: UUID,
    val scopeId: Long,
    val cycleId: Long,
    val openTick: Long,
    val closeTick: Long?,
    val frozen: Boolean = false
)

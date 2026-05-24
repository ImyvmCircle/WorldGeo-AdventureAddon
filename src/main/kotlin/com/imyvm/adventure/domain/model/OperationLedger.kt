package com.imyvm.adventure.domain.model

data class OperationLedger(
    val id: Long,
    val sessionId: Long,
    val channel: String,
    val rawValue: Double,
    val scoredValue: Double,
    val tick: Long
)

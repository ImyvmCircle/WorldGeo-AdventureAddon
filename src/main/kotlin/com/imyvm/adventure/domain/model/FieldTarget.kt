package com.imyvm.adventure.domain.model

data class FieldTarget(
    val scopeId: Long,
    val type: String,
    val intensity: Double,
    val expireTick: Long
)

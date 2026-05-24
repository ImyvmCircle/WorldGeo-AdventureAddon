package com.imyvm.adventure.domain.model

data class MacroIndicator(
    val cycleId: Long,
    val metric: String,
    val value: Double,
    val capturedTick: Long
)

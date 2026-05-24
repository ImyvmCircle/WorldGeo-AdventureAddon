package com.imyvm.adventure.domain.model

data class ScopeIndexSnapshot(
    val scopeId: Long,
    val cycleId: Long,
    val production: Double,
    val anomalyPressure: Double,
    val mortality: Double,
    val missionFailure: Double,
    val capturedTick: Long
)

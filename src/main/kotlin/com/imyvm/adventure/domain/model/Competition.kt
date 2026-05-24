package com.imyvm.adventure.domain.model

data class Competition(
    val id: Long,
    val scopeId: Long,
    val cycleId: Long,
    val pool: Long,
    val openTick: Long,
    val closeTick: Long
)

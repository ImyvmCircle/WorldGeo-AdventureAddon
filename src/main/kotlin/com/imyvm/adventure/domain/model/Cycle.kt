package com.imyvm.adventure.domain.model

data class Cycle(
    val id: Long,
    val weekNumber: Int,
    val openTick: Long,
    val closeTick: Long,
    val closed: Boolean = false
)

package com.imyvm.adventure.domain.math

class HeatField {
    fun recordAction(scopeId: Long, tick: Long, weight: Double) {}

    fun averageHeat(scopeId: Long, windowStart: Long, windowEnd: Long): Double = 0.0

    fun decay(currentTick: Long, halfLifeTicks: Long) {}
}

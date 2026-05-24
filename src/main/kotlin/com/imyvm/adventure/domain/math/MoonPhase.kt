package com.imyvm.adventure.domain.math

object MoonPhase {
    fun phaseAt(tick: Long): Int = 0

    fun weightAt(tick: Long, weights: DoubleArray = DEFAULT_WEIGHTS): Double = 0.0

    fun phaseValue(tick: Long, phi0: Double = 0.0): Double = 0.0

    private val DEFAULT_WEIGHTS = doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
}

package com.imyvm.adventure.domain.math

object CesCap {
    fun playerWeeklyCap(
        operationScore: Double,
        baseCap: Double,
        elasticity: Double
    ): Double = baseCap

    fun communityWeeklyCap(
        developmentScore: Double,
        baseCap: Double,
        elasticity: Double
    ): Double = baseCap

    fun computePlayerAndCommunity(cycleId: Long): Pair<Double, Double> = 0.0 to 0.0
}

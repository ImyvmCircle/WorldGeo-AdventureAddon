package com.imyvm.adventure.domain.math

object MacroIndicators {
    fun gini(values: List<Double>): Double = 0.0

    fun cpiLike(prices: Map<String, Double>, weights: Map<String, Double>): Double = 0.0

    fun activeUserRate(active: Long, total: Long): Double =
        if (total == 0L) 0.0 else active.toDouble() / total.toDouble()
}

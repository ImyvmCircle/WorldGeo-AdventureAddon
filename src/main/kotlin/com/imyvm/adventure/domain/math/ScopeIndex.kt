package com.imyvm.adventure.domain.math

object ScopeIndex {
    fun rawIndex(
        baseAverage: Double,
        avgBase: Double,
        phase: Double,
        avgHeat: Double,
        a: Double,
        b: Double,
        c: Double
    ): Double = baseAverage + a * avgBase + b * phase - c * avgHeat

    fun normalize(rawValues: List<Double>): List<Double> = rawValues
}

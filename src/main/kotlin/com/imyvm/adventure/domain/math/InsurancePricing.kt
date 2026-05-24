package com.imyvm.adventure.domain.math

object InsurancePricing {
    fun premium(
        coverage: Long,
        mortality: Double,
        durationTicks: Long,
        baseRate: Double
    ): Long = 0L

    fun claim(coverage: Long, deductibleRate: Double): Long = 0L
}

package com.imyvm.adventure.domain.math

object SharesPricing {
    fun price(
        scopeIndex: Double,
        outstandingShares: Long,
        liquidity: Double
    ): Double = 0.0

    fun settle(positionValue: Double, currentPrice: Double, shares: Long): Long = 0L
}

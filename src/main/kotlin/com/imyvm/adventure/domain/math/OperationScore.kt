package com.imyvm.adventure.domain.math

object OperationScore {
    fun score(
        rawValue: Double,
        channelWeight: Double,
        scopeIndex: Double,
        difficultyMultiplier: Double
    ): Double = rawValue * channelWeight * scopeIndex * difficultyMultiplier
}

package com.imyvm.adventure.domain.math

object ScopeWeeklyCap {
    fun cap(scopeIndex: Double, baseCap: Double, elasticity: Double): Double = baseCap

    fun applyCap(rawPayout: Double, cap: Double): Double = if (rawPayout > cap) cap else rawPayout
}

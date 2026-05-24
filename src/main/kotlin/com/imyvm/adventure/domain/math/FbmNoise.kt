package com.imyvm.adventure.domain.math

class FbmNoise(
    private val seed: Long,
    private val octaves: Int = 4,
    private val lacunarity: Double = 2.0,
    private val gain: Double = 0.5
) {
    fun sample(x: Double, y: Double, z: Double): Double = 0.0

    fun sample2D(x: Double, y: Double): Double = 0.0
}

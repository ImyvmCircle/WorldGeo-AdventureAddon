package com.imyvm.adventure.domain.math

import com.imyvm.adventure.infra.config.EconomyConfig
import com.imyvm.adventure.infra.config.GameplayConfig
import net.minecraft.server.MinecraftServer
import java.time.Instant

object MoonPhase {
    private const val MINECRAFT_DAY_LENGTH_TICKS: Long = 24000L
    private const val PHASE_COUNT: Int = 8
    private const val FALLBACK_PHASE: Int = 0
    private const val FALLBACK_WEIGHT: Double = 1.0

    @Volatile private var cachedPhase: Int = FALLBACK_PHASE
    @Volatile private var cachedWeight: Double = FALLBACK_WEIGHT
    @Volatile private var cachedScoresProbeAndAerial: Boolean = true
    @Volatile private var cachedDayTime: Long = 0
    @Volatile private var lastRefreshedAtEpochSecond: Long = Long.MIN_VALUE

    fun ensureFresh(server: MinecraftServer) {
        val nowEpochSecond = Instant.now().epochSecond
        val refreshSeconds = GameplayConfig.MOON_PHASE_REFRESH_SECONDS.value.coerceAtLeast(1).toLong()
        if (nowEpochSecond - lastRefreshedAtEpochSecond < refreshSeconds) return
        refresh(server, nowEpochSecond)
    }

    fun forceRefresh(server: MinecraftServer) {
        refresh(server, Instant.now().epochSecond)
    }

    fun currentPhase(): Int = cachedPhase
    fun currentWeight(): Double = cachedWeight
    fun scoresProbeAndAerial(): Boolean = cachedScoresProbeAndAerial
    fun cachedDayTime(): Long = cachedDayTime
    fun lastRefreshedAt(): Long = lastRefreshedAtEpochSecond

    fun phaseAt(tick: Long): Int = ((tick / MINECRAFT_DAY_LENGTH_TICKS) % PHASE_COUNT.toLong()).toInt()

    fun weightAt(tick: Long, weights: List<Double> = EconomyConfig.MOON_PHASE_WEIGHTS.value): Double {
        val phase = phaseAt(tick)
        return weights.getOrNull(phase) ?: FALLBACK_WEIGHT
    }

    private fun refresh(server: MinecraftServer, nowEpochSecond: Long) {
        val overworld = server.overworld()
        val dayTime = overworld.overworldClockTime
        val phase = ((dayTime / MINECRAFT_DAY_LENGTH_TICKS) % PHASE_COUNT.toLong()).toInt().let {
            if (it < 0) it + PHASE_COUNT else it
        }
        val weights = EconomyConfig.MOON_PHASE_WEIGHTS.value
        val mask = GameplayConfig.MOON_PHASE_PROBE_AERIAL_SCORING_MASK.value
        cachedPhase = phase
        cachedWeight = weights.getOrNull(phase) ?: FALLBACK_WEIGHT
        cachedScoresProbeAndAerial = mask.getOrNull(phase) ?: false
        cachedDayTime = dayTime
        lastRefreshedAtEpochSecond = nowEpochSecond
    }
}

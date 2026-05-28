package com.imyvm.adventure.application.service

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.infra.AdventureDatabase
import com.imyvm.adventure.infra.config.AdventureConfig
import com.imyvm.adventure.infra.config.GameplayConfig
import com.imyvm.adventure.infra.config.SettlementConfig
import net.minecraft.server.MinecraftServer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

class AdventureScheduleService {
    private var totalTicks: Long = 0

    fun onServerTick(server: MinecraftServer) {
        totalTicks++

        val heartbeatSeconds = GameplayConfig.SCHEDULER_HEARTBEAT_SECONDS.value.coerceAtLeast(1)
        if (AdventureConfig.DEBUG_LOGGING.value && totalTicks % (heartbeatSeconds * 20L) == 0L) {
            WorldGeoAdventureAddon.logger.debug("Adventure scheduler heartbeat, tick={}", totalTicks)
        }

        val interval = GameplayConfig.RUNTIME_TICK_CHECK_INTERVAL_TICKS.value.coerceAtLeast(1)
        if (totalTicks % interval.toLong() != 0L) return

        val now = nowInZone()
        checkDayFlip(now, server)
        checkWeeklySettlement(now, server)
        MoonPhase.ensureFresh(server)
    }

    fun totalTicks(): Long = totalTicks

    fun zone(): ZoneId = try {
        ZoneId.of(AdventureConfig.TIMEZONE.value)
    } catch (_: Exception) {
        ZoneId.of(DEFAULT_TIMEZONE)
    }

    fun nowInZone(): ZonedDateTime = ZonedDateTime.now(zone())

    fun today(): LocalDate = nowInZone().toLocalDate()

    fun lastSettlementAt(): ZonedDateTime? =
        AdventureDatabase.state.lastSettlementEpochSecond
            ?.let { ZonedDateTime.ofInstant(java.time.Instant.ofEpochSecond(it), zone()) }

    fun nextSettlementAt(base: ZonedDateTime = nowInZone()): ZonedDateTime =
        settlementAfter(base)

    private fun checkDayFlip(now: ZonedDateTime, server: MinecraftServer) {
        val today = now.toLocalDate()
        val stored = AdventureDatabase.state.lastSeenDate?.let { parseLocalDateOrNull(it) }
        if (stored == today) return
        AdventureDatabase.state.lastSeenDate = today.toString()
        onDayFlip(stored, today, server)
    }

    private fun checkWeeklySettlement(now: ZonedDateTime, server: MinecraftServer) {
        var fired = 0
        while (fired < MAX_SETTLEMENT_CATCHUP_PER_TICK) {
            val baseline = lastSettlementAt() ?: now.minusDays(SETTLEMENT_INITIAL_BACKFILL_DAYS)
            val due = settlementAfter(baseline)
            if (now.isBefore(due)) return
            AdventureDatabase.state.lastSettlementEpochSecond = due.toEpochSecond()
            onWeeklySettlement(due, server)
            fired++
        }
    }

    private fun onDayFlip(previous: LocalDate?, today: LocalDate, server: MinecraftServer) {
        WorldGeoAdventureAddon.logger.info(
            "Adventure day flip detected, previous={}, today={}, zone={}",
            previous, today, zone()
        )
    }

    private fun onWeeklySettlement(at: ZonedDateTime, server: MinecraftServer) {
        WorldGeoAdventureAddon.logger.info(
            "Adventure weekly settlement window reached at {}.", at
        )
    }

    private fun settlementAfter(base: ZonedDateTime): ZonedDateTime {
        val zone = zone()
        val weekday = parseWeekday(SettlementConfig.WEEKDAY.value)
        val hour = SettlementConfig.HOUR.value.coerceIn(0, 23)
        val minute = SettlementConfig.MINUTE.value.coerceIn(0, 59)
        val time = LocalTime.of(hour, minute)
        val rebased = base.withZoneSameInstant(zone)
        var candidateDate = rebased.toLocalDate()
        val daysAhead = ((weekday.value - candidateDate.dayOfWeek.value) + 7) % 7
        candidateDate = candidateDate.plusDays(daysAhead.toLong())
        var candidate = ZonedDateTime.of(candidateDate, time, zone)
        if (!candidate.isAfter(rebased)) candidate = candidate.plusDays(7)
        return candidate
    }

    private fun parseWeekday(raw: String): DayOfWeek = try {
        DayOfWeek.valueOf(raw.trim().uppercase())
    } catch (_: IllegalArgumentException) {
        DayOfWeek.SUNDAY
    }

    private fun parseLocalDateOrNull(raw: String): LocalDate? = try {
        LocalDate.parse(raw)
    } catch (_: DateTimeParseException) {
        null
    }

    companion object {
        private const val DEFAULT_TIMEZONE = "Asia/Shanghai"
        private const val MAX_SETTLEMENT_CATCHUP_PER_TICK = 8
        private const val SETTLEMENT_INITIAL_BACKFILL_DAYS = 7L
    }
}

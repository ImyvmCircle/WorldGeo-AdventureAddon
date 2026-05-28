package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class GameplayConfig : HokiConfig("AdventureGameplay.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val SCHEDULER_HEARTBEAT_SECONDS = Option(
            "runtime.scheduler_heartbeat_seconds",
            60,
            "heartbeat interval for the runtime scheduler in debug mode."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val RUNTIME_TICK_CHECK_INTERVAL_TICKS = Option(
            "runtime.tick_check_interval_ticks",
            20,
            "ticks between wall-clock checks driving day flip and weekly settlement."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val MOON_PHASE_REFRESH_SECONDS = Option(
            "moon_phase.refresh_seconds",
            60,
            "minimum seconds between two MoonPhase reads from the overworld."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val MOON_PHASE_PROBE_AERIAL_SCORING_MASK = Option(
            "moon_phase.probe_aerial_scoring_mask",
            listOf(true, true, false, false, false, false, false, true),
            "whether probe and aerial actions score for each Minecraft moon phase 0..7."
        ) { obj, path -> obj.getBooleanList(path).map { it } }
    }
}

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

        @JvmField
        @ConfigOption
        val ANTI_MANIP_CLUSTER_RADIUS_BLOCKS = Option(
            "anti_manipulation.cluster.radius_blocks",
            8,
            "radius in blocks within which same-type scoring events count as a single-spot cluster."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val ANTI_MANIP_CLUSTER_COUNT = Option(
            "anti_manipulation.cluster.count",
            6,
            "number of same-type scoring events inside the cluster window that trips the cluster gate."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val ANTI_MANIP_CLUSTER_WINDOW_SECONDS = Option(
            "anti_manipulation.cluster.window_seconds",
            60,
            "sliding window in seconds for the single-spot cluster gate."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val ANTI_MANIP_POSE_COUNT = Option(
            "anti_manipulation.pose.count",
            3,
            "number of consecutive same-type events sharing a pose that trips the pose gate."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val ANTI_MANIP_POSE_POSITION_BLOCKS = Option(
            "anti_manipulation.pose.position_blocks",
            1.0,
            "maximum position spread in blocks for events to count as a shared pose."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val ANTI_MANIP_POSE_ANGLE_DEGREES = Option(
            "anti_manipulation.pose.angle_degrees",
            3.0,
            "maximum yaw and pitch spread in degrees for events to count as a shared pose."
        ) { obj, path -> obj.getDouble(path) }
    }
}

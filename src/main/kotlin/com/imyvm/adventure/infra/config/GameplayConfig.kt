package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class GameplayConfig : HokiConfig("AdventureGameplay.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val BOARD_REFRESH_MINUTES = Option(
            "cadence.board_refresh_minutes",
            30,
            "board and rumor refresh interval in minutes."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val MARKET_SETTLEMENT_HOURS = Option(
            "cadence.market_settlement_hours",
            6,
            "market settlement cadence in hours."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val CERTIFICATION_DAILY_PLAYER_LOTS = Option(
            "certification.daily_player_lots",
            24,
            "daily certification quota per player for converting vanilla stock into registered adventure lots."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val CERTIFICATION_WEEKLY_PLAYER_LOTS = Option(
            "certification.weekly_player_lots",
            96,
            "weekly certification quota per player for registered adventure lots."
        ) { obj, path ->
            obj.getInt(path)
        }

        @JvmField
        @ConfigOption
        val ANCHOR_MAIN_HAND_ONLY = Option(
            "interaction.anchor_main_hand_only",
            true,
            "whether anchor interactions should only respond to main-hand use."
        ) { obj, path ->
            obj.getBoolean(path)
        }

        @JvmField
        @ConfigOption
        val SCHEDULER_HEARTBEAT_SECONDS = Option(
            "runtime.scheduler_heartbeat_seconds",
            60,
            "debug heartbeat interval for the runtime scheduler."
        ) { obj, path ->
            obj.getInt(path)
        }
    }
}

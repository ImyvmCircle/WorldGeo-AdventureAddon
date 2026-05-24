package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class SettlementConfig : HokiConfig("AdventureSettlement.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val TIMEZONE = Option(
            "cycle.timezone",
            "Asia/Shanghai",
            "time zone used by the weekly settlement scheduler."
        ) { obj, path -> obj.getString(path) }

        @JvmField
        @ConfigOption
        val WEEKDAY = Option(
            "cycle.weekday",
            "Sunday",
            "weekday on which the weekly settlement runs."
        ) { obj, path -> obj.getString(path) }

        @JvmField
        @ConfigOption
        val HOUR = Option(
            "cycle.hour",
            18,
            "hour at which the weekly settlement runs."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val MINUTE = Option(
            "cycle.minute",
            0,
            "minute at which the weekly settlement runs."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val DURATION_MINUTES = Option(
            "cycle.duration_minutes",
            120,
            "maximum duration of the settlement window in minutes."
        ) { obj, path -> obj.getInt(path) }
    }
}

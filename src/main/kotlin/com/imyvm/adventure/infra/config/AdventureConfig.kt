package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class AdventureConfig : HokiConfig("Adventure.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val LANGUAGE = Option(
            "language",
            "zh_cn",
            "the display language of the adventure addon."
        ) { obj, path ->
            obj.getString(path)
        }

        @JvmField
        @ConfigOption
        val TIMEZONE = Option(
            "timezone",
            "Asia/Shanghai",
            "the time zone used by cadence and rotation features."
        ) { obj, path ->
            obj.getString(path)
        }

        @JvmField
        @ConfigOption
        val DEBUG_LOGGING = Option(
            "debug.logging",
            false,
            "whether to emit periodic debug information for the runtime skeleton."
        ) { obj, path ->
            obj.getBoolean(path)
        }
    }
}

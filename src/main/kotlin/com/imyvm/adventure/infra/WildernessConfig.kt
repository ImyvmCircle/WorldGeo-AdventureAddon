package com.imyvm.adventure.infra

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class WildernessConfig : HokiConfig("Wilderness.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val LANGUAGE = Option(
            "language",
            "zh_cn",
            "the language of the mod."
        ) { obj, path ->
            obj.getString(path)
        }

        @JvmField
        @ConfigOption
        val TIMEZONE = Option(
            "timezone",
            "Asia/Hong_Kong",
            "the time zone of the mod."
        ) { obj, path ->
            obj.getString(path)
        }

        fun validateValues() {
            require(LANGUAGE.value.isNotBlank()) { "language must not be blank" }
            java.time.ZoneId.of(TIMEZONE.value)
        }
    }
}

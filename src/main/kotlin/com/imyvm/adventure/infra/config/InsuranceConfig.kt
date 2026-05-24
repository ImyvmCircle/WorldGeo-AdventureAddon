package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class InsuranceConfig : HokiConfig("AdventureInsurance.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val BASE_RATE = Option(
            "pricing.base_rate",
            100,
            "base premium for a single cycle of insurance."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val GAMMA_RISK = Option(
            "pricing.gamma_risk",
            0.80,
            "risk sensitivity coefficient gamma_risk."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val GAMMA_PRESSURE = Option(
            "pricing.gamma_pressure",
            0.40,
            "pressure sensitivity coefficient gamma_pressure."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val GAMMA_HISTORY = Option(
            "pricing.gamma_history",
            0.60,
            "history sensitivity coefficient gamma_history."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val HISTORY_WINDOW_WEEKS = Option(
            "pricing.history_window_weeks",
            4,
            "lookback window in weeks for history-based pricing."
        ) { obj, path -> obj.getInt(path) }
    }
}

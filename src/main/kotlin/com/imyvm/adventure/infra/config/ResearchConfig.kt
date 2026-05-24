package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class ResearchConfig : HokiConfig("AdventureResearch.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val TIER_COUNT = Option(
            "tier.count",
            6,
            "number of research tiers."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val SAMPLE_BOUNTY_MIN = Option(
            "sample.bounty_min",
            50,
            "minimum sample bounty payout."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val SAMPLE_BOUNTY_MAX = Option(
            "sample.bounty_max",
            1500,
            "maximum sample bounty payout."
        ) { obj, path -> obj.getInt(path) }

        @JvmField
        @ConfigOption
        val SAMPLE_BOUNTY_FORMULA = Option(
            "sample.bounty_formula",
            "linear_on_sample_value",
            "formula key used to derive sample bounty."
        ) { obj, path -> obj.getString(path) }
    }
}

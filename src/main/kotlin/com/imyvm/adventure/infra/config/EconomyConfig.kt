package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class EconomyConfig : HokiConfig("AdventureEconomy.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val PLAYER_CES_RHO = Option(
            "player_ces.rho",
            -1.0,
            "CES elasticity rho for the player weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val PLAYER_CES_ETA = Option(
            "player_ces.eta",
            0.70,
            "CES weight eta for the player weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val PLAYER_BREAK_EVEN_RATIO = Option(
            "player_ces.break_even_ratio",
            0.60,
            "break-even ratio for the player weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val COMMUNITY_CES_RHO = Option(
            "community_ces.rho",
            -1.0,
            "CES elasticity rho for the community weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val COMMUNITY_CES_ETA = Option(
            "community_ces.eta",
            0.60,
            "CES weight eta for the community weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val COMMUNITY_BREAK_EVEN_RATIO = Option(
            "community_ces.break_even_ratio",
            0.85,
            "break-even ratio for the community weekly cap."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val MOON_PHASE_WEIGHTS = Option(
            "moon_phase.phase_weights",
            listOf(1.0, 0.7, 0.4, 0.2, 0.1, 0.2, 0.4, 0.7),
            "phase_weight multiplier indexed by Minecraft moon phase 0..7."
        ) { obj, path -> obj.getDoubleList(path).map { it.toDouble() } }
    }
}

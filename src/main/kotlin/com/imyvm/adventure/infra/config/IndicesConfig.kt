package com.imyvm.adventure.infra.config

import com.imyvm.hoki.config.ConfigOption
import com.imyvm.hoki.config.HokiConfig
import com.imyvm.hoki.config.Option

class IndicesConfig : HokiConfig("AdventureIndices.conf") {
    companion object {
        @JvmField
        @ConfigOption
        val NOISE_SEED = Option(
            "noise.seed_world",
            1399811919L,
            "world seed used by fbm noise; reset per season if needed."
        ) { obj, path -> obj.getLong(path) }

        @JvmField
        @ConfigOption
        val NOISE_LACUNARITY = Option(
            "noise.lacunarity",
            2.0,
            "fbm lacunarity factor."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val NOISE_PERSISTENCE = Option(
            "noise.persistence",
            0.5,
            "fbm persistence factor."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val WEIGHT_NOISE = Option(
            "raw_weights.a",
            0.40,
            "weight a applied to the noise component of the scope index."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val WEIGHT_MOON = Option(
            "raw_weights.b",
            0.20,
            "weight b applied to the moon phase component of the scope index."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val WEIGHT_HEAT = Option(
            "raw_weights.c",
            0.50,
            "weight c applied to the heat field deduction of the scope index."
        ) { obj, path -> obj.getDouble(path) }

        @JvmField
        @ConfigOption
        val HEAT_HALF_LIFE_DAYS = Option(
            "heat.tau_heat_days",
            3,
            "heat field half-life in days."
        ) { obj, path -> obj.getInt(path) }
    }
}

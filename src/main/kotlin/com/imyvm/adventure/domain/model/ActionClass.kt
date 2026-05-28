package com.imyvm.adventure.domain.model

enum class ActionClass(val configKey: String, val defaultWeight: Double) {
    PROBE("probe", 0.6),
    SAMPLE("sample", 0.8),
    COMBAT("combat", 1.0),
    PUZZLE_VAULT("puzzle_vault", 1.2),
    AERIAL("aerial", 1.3),
    LOGISTICS_TRADE("logistics_trade", 0.7);

    companion object {
        fun fromKey(key: String): ActionClass? = entries.firstOrNull { it.configKey == key }
    }
}

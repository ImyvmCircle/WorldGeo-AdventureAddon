package com.imyvm.adventure.domain.model

enum class ActionClass(
    val configKey: String,
    val defaultWeight: Double,
    val defaultAllowanceFraction: Double
) {
    PROBE("probe", 0.6, 0.10),
    SAMPLE("sample", 0.8, 0.05),
    COMBAT("combat", 1.0, 0.10),
    CACHE("cache", 1.2, 0.20),
    AERIAL("aerial", 1.3, 0.10),
    LOGISTICS_TRADE("logistics_trade", 0.7, 0.30);

    companion object {
        fun fromKey(key: String): ActionClass? = entries.firstOrNull { it.configKey == key }
    }
}

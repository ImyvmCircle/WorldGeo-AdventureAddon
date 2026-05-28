package com.imyvm.adventure.domain.model

enum class ActionEventType(
    val configKey: String,
    val actionClass: ActionClass,
    val defaultBaseScore: Double
) {
    READ("read", ActionClass.PROBE, 1.0),
    SAMPLE_BLOCK("sample_block", ActionClass.SAMPLE, 1.0),
    SAMPLE_ENTITY("sample_entity", ActionClass.SAMPLE, 1.0),
    BRUSH("brush", ActionClass.SAMPLE, 1.0),
    COMBAT("combat", ActionClass.COMBAT, 1.0),
    PUZZLE("puzzle", ActionClass.PUZZLE_VAULT, 1.0),
    VAULT("vault", ActionClass.PUZZLE_VAULT, 1.0),
    CHEST("chest", ActionClass.PUZZLE_VAULT, 1.0),
    AIR_HIT("air_hit", ActionClass.AERIAL, 1.0),
    AIR_HAUL("air_haul", ActionClass.AERIAL, 1.0),
    LOGISTICS("logistics", ActionClass.LOGISTICS_TRADE, 1.0),
    TRADE("trade", ActionClass.LOGISTICS_TRADE, 1.0);

    companion object {
        fun fromKey(key: String): ActionEventType? = entries.firstOrNull { it.configKey == key }
    }
}

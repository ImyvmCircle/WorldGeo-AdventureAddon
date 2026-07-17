package com.imyvm.adventure.domain.model.wilderness

class Wilderness(
    val regionNumberId: Int,
    var name: String,
    var status: WildernessStatus,
    var creationTime: Long
)

enum class WildernessStatus(val value: Int) {
    ACTIVE(0),
    FROZEN(1);

    companion object {
        fun fromValue(value: Int): WildernessStatus =
            entries.first { it.value == value }
    }
}

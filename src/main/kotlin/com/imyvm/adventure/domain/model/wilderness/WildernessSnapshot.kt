package com.imyvm.adventure.domain.model.wilderness

data class WildernessSnapshot(
    val regionNumberId: Int,
    val name: String,
    val status: WildernessStatus,
    val creationTime: Long
)

fun Wilderness.toSnapshot(): WildernessSnapshot = WildernessSnapshot(
    regionNumberId = regionNumberId,
    name = name,
    status = status,
    creationTime = creationTime
)

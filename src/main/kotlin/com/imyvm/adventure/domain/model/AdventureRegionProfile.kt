package com.imyvm.adventure.domain.model

data class AdventureRegionProfile(
    val regionNumberId: Int,
    var displayName: String,
    val tags: MutableSet<String> = linkedSetOf(),
    val metadata: MutableMap<String, String> = linkedMapOf()
)

package com.imyvm.adventure.domain.model

data class AdventureProject(
    val projectId: String,
    val regionNumberId: Int,
    var displayName: String,
    var phase: String = "PRIMARY_SUBSCRIPTION",
    var status: String = "PLANNED",
    var shareIssueId: String? = null
)

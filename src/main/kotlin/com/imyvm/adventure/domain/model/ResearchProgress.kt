package com.imyvm.adventure.domain.model

data class ResearchProgress(
    val id: Long,
    val fundingId: Long,
    val milestone: Int,
    val progress: Double,
    val completedTick: Long?
)

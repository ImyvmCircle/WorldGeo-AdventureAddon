package com.imyvm.adventure.domain.model

import java.util.UUID

data class ResearchFunding(
    val id: Long,
    val sponsorUuid: UUID,
    val scopeId: Long,
    val amount: Long,
    val openTick: Long
)

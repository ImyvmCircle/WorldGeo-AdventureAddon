package com.imyvm.adventure.application.service

import com.imyvm.adventure.domain.model.InsurancePolicy
import java.util.UUID

class InsuranceService {
    fun purchase(playerUuid: UUID, scopeId: Long, tier: String): InsurancePolicy? = null
    fun onPlayerDeath(playerUuid: UUID, scopeId: Long): Long = 0L
}

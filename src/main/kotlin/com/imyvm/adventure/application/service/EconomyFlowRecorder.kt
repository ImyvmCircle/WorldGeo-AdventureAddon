package com.imyvm.adventure.application.service

import com.imyvm.adventure.domain.model.EconomyFlow
import java.util.UUID

class EconomyFlowRecorder {
    fun record(channel: String, playerUuid: UUID?, scopeId: Long?, amount: Long, tick: Long): EconomyFlow? = null
    fun listByCycle(cycleId: Long): List<EconomyFlow> = emptyList()
}

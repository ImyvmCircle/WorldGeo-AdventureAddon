package com.imyvm.adventure.application.service

import com.imyvm.adventure.domain.model.OperationSession
import java.util.UUID

class SessionManager {
    fun open(playerUuid: UUID, scopeId: Long, cycleId: Long, tick: Long): OperationSession? = null
    fun close(sessionId: Long, tick: Long) {}
    fun freezeAll(cycleId: Long) {}
    fun listActive(playerUuid: UUID): List<OperationSession> = emptyList()
}

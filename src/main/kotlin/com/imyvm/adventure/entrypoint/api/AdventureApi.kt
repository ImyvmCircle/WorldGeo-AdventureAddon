package com.imyvm.adventure.entrypoint.api

import com.imyvm.adventure.domain.model.OperationSession
import com.imyvm.adventure.domain.model.WindowPhase
import java.util.UUID

object AdventureApi {
    fun getScopeIndex(scopeId: Long, indexType: String, tick: Long): Double = 0.0

    fun getCurrentWindowPhase(scopeId: Long): WindowPhase = WindowPhase.CLOSED

    fun listActiveSessions(playerUuid: UUID): List<OperationSession> = emptyList()

    fun queryPlayerWeekScore(playerUuid: UUID, cycleId: Long): Long = 0L

    fun queryCommunityWeekRevenue(regionNumberId: Int, cycleId: Long): Long = 0L

    fun subscribeIndexChange(listener: IndexChangeListener) {
        indexChangeListeners.add(listener)
    }

    fun subscribePhaseChange(listener: PhaseChangeListener) {
        phaseChangeListeners.add(listener)
    }

    private val indexChangeListeners = mutableListOf<IndexChangeListener>()
    private val phaseChangeListeners = mutableListOf<PhaseChangeListener>()

    fun interface IndexChangeListener {
        fun onIndexChange(scopeId: Long, indexType: String, newValue: Double)
    }

    fun interface PhaseChangeListener {
        fun onPhaseChange(scopeId: Long, previous: WindowPhase, current: WindowPhase)
    }
}

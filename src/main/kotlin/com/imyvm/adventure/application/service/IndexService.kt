package com.imyvm.adventure.application.service

import com.imyvm.adventure.domain.model.ScopeIndexSnapshot

class IndexService {
    fun computeAll(cycleId: Long): List<ScopeIndexSnapshot> = emptyList()
    fun valueAt(scopeId: Long, indexType: String, tick: Long): Double = 0.0
}

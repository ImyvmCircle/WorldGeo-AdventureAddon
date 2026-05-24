package com.imyvm.adventure.application.service

import com.imyvm.adventure.domain.model.WindowPhase

class WindowScheduler {
    fun currentPhase(scopeId: Long): WindowPhase = WindowPhase.CLOSED
    fun advance(currentTick: Long) {}
}

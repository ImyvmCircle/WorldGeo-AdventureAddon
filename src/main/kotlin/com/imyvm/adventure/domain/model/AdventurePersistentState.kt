package com.imyvm.adventure.domain.model

import java.time.LocalDate
import java.util.UUID

class AdventurePersistentState {
    var schemaVersion: Int = 1
    var lastSeenDate: String? = null
    var lastSettlementEpochSecond: Long? = null
    val dailyOperationScores: MutableMap<String, Double> = mutableMapOf()

    fun addDailyOperationScore(playerUuid: UUID, actionClass: ActionClass, date: LocalDate, opScore: Double) {
        if (opScore <= 0.0) return
        val key = dailyOperationScoreKey(playerUuid, actionClass, date)
        dailyOperationScores[key] = (dailyOperationScores[key] ?: 0.0) + opScore
    }

    companion object {
        fun dailyOperationScoreKey(playerUuid: UUID, actionClass: ActionClass, date: LocalDate): String =
            "$playerUuid|${actionClass.name}|$date"
    }
}

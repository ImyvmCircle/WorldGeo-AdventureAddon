package com.imyvm.adventure.domain.model

class AdventurePersistentState {
    var schemaVersion: Int = 1
    var lastSeenDate: String? = null
    var lastSettlementEpochSecond: Long? = null
}

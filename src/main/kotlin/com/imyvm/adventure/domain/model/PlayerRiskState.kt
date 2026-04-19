package com.imyvm.adventure.domain.model

data class PlayerRiskState(
    val playerUuid: String,
    var dailyCertifiedLots: Int = 0,
    var weeklyCertifiedLots: Int = 0,
    var tradesThisSettlement: Int = 0,
    var shareExposureBasisPoints: Int = 0
)

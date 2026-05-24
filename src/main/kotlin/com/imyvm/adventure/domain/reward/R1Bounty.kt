package com.imyvm.adventure.domain.reward

import java.util.UUID

object R1Bounty {
    fun pay(playerUuid: UUID, action: String, amount: Long): Long = amount
}

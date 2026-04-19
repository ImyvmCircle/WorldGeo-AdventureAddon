package com.imyvm.adventure.application.service

import com.imyvm.economy.EconomyMod
import net.minecraft.server.level.ServerPlayer

class AdventureEconomyService {
    fun getPlayerBalance(player: ServerPlayer): Long =
        EconomyMod.data.getOrCreate(player).money

    fun canAfford(player: ServerPlayer, amount: Long): Boolean =
        getPlayerBalance(player) >= amount

    fun addMoney(player: ServerPlayer, amount: Long) {
        EconomyMod.data.getOrCreate(player).addMoney(amount)
    }
}

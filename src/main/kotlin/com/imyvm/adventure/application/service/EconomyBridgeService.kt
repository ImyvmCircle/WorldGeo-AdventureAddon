package com.imyvm.adventure.application.service

import com.imyvm.economy.api.DatabaseApi
import net.minecraft.server.level.ServerPlayer

class EconomyBridgeService {
    fun balance(player: ServerPlayer): Long =
        DatabaseApi.getInstance().getPlayer(player).money

    fun deposit(player: ServerPlayer, amount: Long): Boolean {
        if (amount <= 0L) return false
        DatabaseApi.getInstance().getPlayer(player).addMoney(amount)
        return true
    }

    fun withdraw(player: ServerPlayer, amount: Long): Boolean {
        if (amount <= 0L) return false
        return DatabaseApi.getInstance().getPlayer(player).takeMoney(amount)
    }
}

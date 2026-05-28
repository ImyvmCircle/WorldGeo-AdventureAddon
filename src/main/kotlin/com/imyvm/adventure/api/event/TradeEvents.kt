package com.imyvm.adventure.api.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.trading.Merchant

object TradeEvents {
    @JvmField
    val ON_TRADE_COMPLETED: Event<TradeCompletedCallback> = EventFactory.createArrayBacked(
        TradeCompletedCallback::class.java
    ) { listeners ->
        TradeCompletedCallback { player, result, merchant ->
            for (listener in listeners) {
                listener.onTradeCompleted(player, result, merchant)
            }
        }
    }

    fun interface TradeCompletedCallback {
        fun onTradeCompleted(player: ServerPlayer, result: ItemStack, merchant: Merchant)
    }
}

package com.imyvm.adventure.application.listener

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.api.event.TradeEvents
import net.minecraft.core.registries.BuiltInRegistries

class TradeListener {
    fun register() {
        TradeEvents.ON_TRADE_COMPLETED.register { player, result, merchant ->
            WorldGeoAdventureAddon.logger.info(
                "[adventure.trade] player={} item={} count={} merchant={}",
                player.scoreboardName,
                BuiltInRegistries.ITEM.getKey(result.item),
                result.count,
                merchant::class.java.simpleName
            )
        }
    }
}

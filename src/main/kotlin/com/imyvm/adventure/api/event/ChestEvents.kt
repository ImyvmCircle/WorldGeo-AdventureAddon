package com.imyvm.adventure.api.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.RandomizableContainer

object ChestEvents {
    @JvmField
    val ON_CHEST_LOOT_UNPACKED: Event<ChestLootUnpackedCallback> = EventFactory.createArrayBacked(
        ChestLootUnpackedCallback::class.java
    ) { listeners ->
        ChestLootUnpackedCallback { player, container ->
            for (listener in listeners) {
                listener.onChestLootUnpacked(player, container)
            }
        }
    }

    fun interface ChestLootUnpackedCallback {
        fun onChestLootUnpacked(player: ServerPlayer, container: RandomizableContainer)
    }
}

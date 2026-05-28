package com.imyvm.adventure.api.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.entity.BrushableBlockEntity

object BrushEvents {
    @JvmField
    val ON_BRUSHING_COMPLETED: Event<BrushingCompletedCallback> = EventFactory.createArrayBacked(
        BrushingCompletedCallback::class.java
    ) { listeners ->
        BrushingCompletedCallback { player, level, blockEntity ->
            for (listener in listeners) {
                listener.onBrushingCompleted(player, level, blockEntity)
            }
        }
    }

    fun interface BrushingCompletedCallback {
        fun onBrushingCompleted(player: ServerPlayer, level: ServerLevel, blockEntity: BrushableBlockEntity)
    }
}

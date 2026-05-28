package com.imyvm.adventure.api.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerPlayer

object ProbeEvents {
    @JvmField
    val ON_SPYGLASS_USED: Event<SpyglassUsedCallback> = EventFactory.createArrayBacked(
        SpyglassUsedCallback::class.java
    ) { listeners ->
        SpyglassUsedCallback { player ->
            for (listener in listeners) {
                listener.onSpyglassUsed(player)
            }
        }
    }

    fun interface SpyglassUsedCallback {
        fun onSpyglassUsed(player: ServerPlayer)
    }
}

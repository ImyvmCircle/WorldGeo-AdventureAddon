package com.imyvm.adventure.api.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity

object AerialEvents {
    @JvmField
    val ON_AIR_HIT: Event<AirHitCallback> = EventFactory.createArrayBacked(
        AirHitCallback::class.java
    ) { listeners ->
        AirHitCallback { player, victim ->
            for (listener in listeners) listener.onAirHit(player, victim)
        }
    }

    @JvmField
    val ON_AIR_HAUL: Event<AirHaulCallback> = EventFactory.createArrayBacked(
        AirHaulCallback::class.java
    ) { listeners ->
        AirHaulCallback { player, distance ->
            for (listener in listeners) listener.onAirHaul(player, distance)
        }
    }

    fun interface AirHitCallback {
        fun onAirHit(player: ServerPlayer, victim: LivingEntity)
    }

    fun interface AirHaulCallback {
        fun onAirHaul(player: ServerPlayer, distance: Double)
    }
}

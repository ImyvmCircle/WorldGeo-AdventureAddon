package com.imyvm.adventure.application.service

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.infra.config.EconomyConfig
import net.minecraft.server.MinecraftServer

class RareCacheService(
    private val scopeResolver: ScopeResolver
) {
    private val activeByRegion: MutableMap<Int, ActiveCacheState> = mutableMapOf()
    private val random = java.util.Random()

    fun tick(server: MinecraftServer) {
        val phaseWeight = MoonPhase.currentWeight()
        val spawnBase = EconomyConfig.RARE_CACHE_SPAWN_BASE_PER_TICK.value
        val decayBase = EconomyConfig.RARE_CACHE_DECAY_BASE_PER_TICK.value
        val pSpawn = spawnBase * phaseWeight
        val pDecay = decayBase * (1.0 - phaseWeight)

        for (region in scopeResolver.listAdventureRegions()) {
            val regionId = region.numberID
            val active = activeByRegion[regionId]
            if (active == null) {
                if (random.nextDouble() < pSpawn) {
                    activeByRegion[regionId] = ActiveCacheState(server.tickCount.toLong())
                    WorldGeoAdventureAddon.logger.info(
                        "[adventure.rare_cache] spawn region={} regionId={} pSpawn={} phase={}",
                        region.name, regionId, pSpawn, phaseWeight
                    )
                }
            } else {
                if (random.nextDouble() < pDecay) {
                    activeByRegion.remove(regionId)
                    WorldGeoAdventureAddon.logger.info(
                        "[adventure.rare_cache] decay region={} regionId={} ageTicks={} pDecay={} phase={}",
                        region.name, regionId, server.tickCount.toLong() - active.spawnedAtTick, pDecay, phaseWeight
                    )
                }
            }
        }
    }

    fun isActive(regionId: Int): Boolean = activeByRegion.containsKey(regionId)

    fun activeCount(): Int = activeByRegion.size

    data class ActiveCacheState(val spawnedAtTick: Long)
}

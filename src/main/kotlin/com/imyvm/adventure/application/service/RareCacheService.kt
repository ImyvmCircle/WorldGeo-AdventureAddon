package com.imyvm.adventure.application.service

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.domain.math.MoonPhase
import com.imyvm.adventure.infra.config.EconomyConfig
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
import net.minecraft.world.level.levelgen.Heightmap

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
        val radius = EconomyConfig.RARE_CACHE_SPAWN_RADIUS.value

        for (region in scopeResolver.listAdventureRegions()) {
            val regionId = region.numberID
            val active = activeByRegion[regionId]
            if (active == null) {
                if (random.nextDouble() < pSpawn) {
                    trySpawn(server, region.numberID, region.name, radius, pSpawn, phaseWeight)
                }
            } else {
                if (random.nextDouble() < pDecay) {
                    despawn(server, region.name, regionId, active, pDecay, phaseWeight)
                }
            }
        }
    }

    fun isActive(regionId: Int): Boolean = activeByRegion.containsKey(regionId)

    fun activeCount(): Int = activeByRegion.size

    fun activeAt(level: ServerLevel, pos: BlockPos): ActiveCacheState? {
        val dim = level.dimension()
        return activeByRegion.values.firstOrNull { it.dimension == dim && it.pos == pos }
    }

    private fun trySpawn(
        server: MinecraftServer,
        regionId: Int,
        regionName: String,
        radius: Int,
        pSpawn: Double,
        phaseWeight: Double
    ) {
        val anchor = pickAnchorPlayer(server, regionId) ?: return
        val level = anchor.level() as? ServerLevel ?: return
        val dx = random.nextInt(radius * 2 + 1) - radius
        val dz = random.nextInt(radius * 2 + 1) - radius
        val x = anchor.blockX + dx
        val z = anchor.blockZ + dz
        val y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z)
        val pos = BlockPos(x, y, z)

        if (!level.getBlockState(pos.below()).isSolid) return
        if (!level.getBlockState(pos).isAir) return

        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3)
        val be = level.getBlockEntity(pos) as? RandomizableContainerBlockEntity
        if (be == null) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
            return
        }
        val lootKey = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("adventure", "rare_cache"))
        be.setLootTable(lootKey, random.nextLong())
        be.setChanged()

        val biome = level.getBiome(pos).unwrapKey().map { it.identifier().toString() }.orElse("unknown")
        val state = ActiveCacheState(
            spawnedAtTick = server.tickCount.toLong(),
            dimension = level.dimension(),
            pos = pos,
            biome = biome,
            regionName = regionName
        )
        activeByRegion[regionId] = state
        broadcastSpawn(server, state)

        WorldGeoAdventureAddon.logger.info(
            "[adventure.rare_cache] spawn region={} regionId={} pos={} biome={} pSpawn={} phase={}",
            regionName, regionId, pos, biome, pSpawn, phaseWeight
        )
    }

    private fun despawn(
        server: MinecraftServer,
        regionName: String,
        regionId: Int,
        active: ActiveCacheState,
        pDecay: Double,
        phaseWeight: Double
    ) {
        val level = server.getLevel(active.dimension)
        if (level != null) {
            val state = level.getBlockState(active.pos)
            if (state.`is`(Blocks.CHEST)) {
                level.setBlock(active.pos, Blocks.AIR.defaultBlockState(), 3)
            }
        }
        activeByRegion.remove(regionId)
        broadcastDespawn(server, regionName, active)

        WorldGeoAdventureAddon.logger.info(
            "[adventure.rare_cache] decay region={} regionId={} ageTicks={} pDecay={} phase={}",
            regionName, regionId, server.tickCount.toLong() - active.spawnedAtTick, pDecay, phaseWeight
        )
    }

    private fun pickAnchorPlayer(server: MinecraftServer, regionId: Int): ServerPlayer? {
        val candidates = mutableListOf<ServerPlayer>()
        for (player in scopeResolver.listOnlinePlayersInAdventure(server)) {
            val loc = scopeResolver.resolveAdventureLocation(player) ?: continue
            if (loc.region.numberID == regionId) candidates.add(player)
        }
        if (candidates.isEmpty()) return null
        return candidates[random.nextInt(candidates.size)]
    }

    private fun broadcastSpawn(server: MinecraftServer, state: ActiveCacheState) {
        val xBucket = Math.floorDiv(state.pos.x, 100) * 100
        val zBucket = Math.floorDiv(state.pos.z, 100) * 100
        val msg = "[Adventure] Rare cache appeared in ${state.regionName}, x ∈ [$xBucket, ${xBucket + 100}), z ∈ [$zBucket, ${zBucket + 100}), biome ${state.biome}."
        server.playerList.broadcastSystemMessage(Component.literal(msg), false)
    }

    private fun broadcastDespawn(server: MinecraftServer, regionName: String, state: ActiveCacheState) {
        val xBucket = Math.floorDiv(state.pos.x, 100) * 100
        val zBucket = Math.floorDiv(state.pos.z, 100) * 100
        val msg = "[Adventure] Rare cache decayed in $regionName, x ∈ [$xBucket, ${xBucket + 100}), z ∈ [$zBucket, ${zBucket + 100})."
        server.playerList.broadcastSystemMessage(Component.literal(msg), false)
    }

    data class ActiveCacheState(
        val spawnedAtTick: Long,
        val dimension: ResourceKey<net.minecraft.world.level.Level>,
        val pos: BlockPos,
        val biome: String,
        val regionName: String
    )
}

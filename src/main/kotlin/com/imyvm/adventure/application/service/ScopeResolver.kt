package com.imyvm.adventure.application.service

import com.imyvm.iwg.domain.Region
import com.imyvm.iwg.domain.component.GeoScope
import com.imyvm.iwg.inter.api.RegionDataApi
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

class ScopeResolver(
    private val worldGeoBridgeService: WorldGeoBridgeService
) {
    fun resolveAt(level: Level, blockPos: BlockPos): ResolvedAdventureLocation? =
        worldGeoBridgeService.resolveAt(level, blockPos)

    fun resolveForPlayer(player: ServerPlayer): ResolvedAdventureLocation? =
        worldGeoBridgeService.resolveForPlayer(player)

    fun resolveForEntity(entity: Entity): ResolvedAdventureLocation? =
        resolveAt(entity.level(), entity.blockPosition())

    fun resolveAdventureLocation(player: ServerPlayer): ResolvedAdventureLocation? {
        val loc = resolveForPlayer(player) ?: return null
        val adventureIds = listAdventureRegions().mapTo(HashSet()) { it.numberID }
        return if (loc.region.numberID in adventureIds) loc else null
    }

    fun getRegion(regionNumberId: Int): Region? =
        worldGeoBridgeService.getRegion(regionNumberId)

    fun listScopesInRegion(regionNumberId: Int): List<GeoScope> =
        getRegion(regionNumberId)?.geometryScope?.toList() ?: emptyList()

    fun listAdventureRegions(): List<Region> =
        RegionDataApi.getRegionListFiltered(ADVENTURE_REGION_MARK)

    companion object {
        const val ADVENTURE_REGION_MARK: Int = 1
    }
}

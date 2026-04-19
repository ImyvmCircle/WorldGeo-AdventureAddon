package com.imyvm.adventure.application.service

import com.imyvm.iwg.domain.Region
import com.imyvm.iwg.domain.component.GeoScope
import com.imyvm.iwg.inter.api.RegionDataApi
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

data class ResolvedAdventureLocation(
    val region: Region,
    val scope: GeoScope?
)

class WorldGeoBridgeService {
    fun getRegion(regionNumberId: Int): Region? =
        RegionDataApi.getRegion(regionNumberId)

    fun resolveAt(level: Level, blockPos: BlockPos): ResolvedAdventureLocation? =
        RegionDataApi.getRegionScopePairByLocation(level, blockPos)?.let { pair ->
            ResolvedAdventureLocation(
                region = pair.first,
                scope = pair.second
            )
        }
}

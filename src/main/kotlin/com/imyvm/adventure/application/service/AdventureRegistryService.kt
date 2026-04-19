package com.imyvm.adventure.application.service

import com.imyvm.adventure.domain.model.AdventureAnchor
import com.imyvm.adventure.domain.model.AdventureProject
import com.imyvm.adventure.domain.model.AdventureRegionProfile
import com.imyvm.adventure.infra.AdventureDatabase
import net.minecraft.core.BlockPos

class AdventureRegistryService {
    fun countRegions(): Int = AdventureDatabase.state.regionProfiles.size

    fun countAnchors(): Int = AdventureDatabase.state.anchors.size

    fun countProjects(): Int = AdventureDatabase.state.projects.size

    fun listAnchors(): List<AdventureAnchor> =
        AdventureDatabase.state.anchors.values.toList()

    fun findAnchor(worldId: String, blockPos: BlockPos): AdventureAnchor? =
        AdventureDatabase.state.anchors.values.firstOrNull { it.matches(worldId, blockPos) }

    fun upsertAnchor(anchor: AdventureAnchor) {
        AdventureDatabase.state.anchors[anchor.anchorId] = anchor
    }

    fun getOrCreateRegionProfile(regionNumberId: Int, displayName: String): AdventureRegionProfile {
        val existing = AdventureDatabase.state.regionProfiles[regionNumberId]
        if (existing != null) {
            if (existing.displayName.isBlank()) {
                existing.displayName = displayName
            }
            return existing
        }

        return AdventureRegionProfile(
            regionNumberId = regionNumberId,
            displayName = displayName
        ).also {
            AdventureDatabase.state.regionProfiles[regionNumberId] = it
        }
    }

    fun upsertProject(project: AdventureProject) {
        AdventureDatabase.state.projects[project.projectId] = project
    }
}

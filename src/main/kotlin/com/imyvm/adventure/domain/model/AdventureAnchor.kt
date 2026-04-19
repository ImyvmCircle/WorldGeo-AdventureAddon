package com.imyvm.adventure.domain.model

import net.minecraft.core.BlockPos

data class AdventureAnchor(
    val anchorId: String,
    val regionNumberId: Int,
    val scopeName: String? = null,
    var title: String,
    val worldId: String,
    val x: Int,
    val y: Int,
    val z: Int,
    var kind: String,
    var enabled: Boolean = true,
    val metadata: MutableMap<String, String> = linkedMapOf()
) {
    fun matches(targetWorldId: String, blockPos: BlockPos): Boolean =
        enabled &&
            worldId == targetWorldId &&
            x == blockPos.x &&
            y == blockPos.y &&
            z == blockPos.z
}

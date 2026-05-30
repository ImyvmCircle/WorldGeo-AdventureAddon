package com.imyvm.adventure.application.service

import com.imyvm.adventure.domain.model.ActionEventType
import com.imyvm.adventure.infra.config.GameplayConfig
import net.minecraft.server.level.ServerPlayer
import java.util.UUID
import kotlin.math.abs
import kotlin.math.floor

class SessionManager {
    private class Activity(
        val eventType: ActionEventType,
        val x: Double,
        val y: Double,
        val z: Double,
        val yaw: Float,
        val pitch: Float,
        val regionId: Int,
        val tick: Long
    )

    private val recent: MutableMap<UUID, ArrayDeque<Activity>> = HashMap()

    fun shouldSuppress(player: ServerPlayer, eventType: ActionEventType, regionId: Int, tick: Long): Boolean {
        val log = recent.getOrPut(player.uuid) { ArrayDeque() }
        val current = Activity(eventType, player.x, player.y, player.z, player.yRot, player.xRot, regionId, tick)

        while (log.isNotEmpty() && log.first().tick < tick - retentionTicks()) log.removeFirst()
        log.addLast(current)
        while (log.size > MAX_TRACKED) log.removeFirst()

        return clusterSuppressed(log, current) || poseSuppressed(log, current)
    }

    fun heatPenalty(player: ServerPlayer, eventType: ActionEventType, tick: Long): Double {
        val log = recent[player.uuid] ?: return 0.0
        val saturate = GameplayConfig.ANTI_MANIP_HEAT_SATURATE.value
        if (saturate <= 0) return 0.0
        val cap = GameplayConfig.ANTI_MANIP_HEAT_CAP.value
        val windowTicks = GameplayConfig.ANTI_MANIP_HEAT_WINDOW_SECONDS.value.coerceAtLeast(1) * 20L
        val minTick = tick - windowTicks
        val cx = floor(player.x).toInt() shr 4
        val cz = floor(player.z).toInt() shr 4
        var n = 0
        for (a in log) {
            if (a.tick < minTick || a.eventType != eventType) continue
            if ((floor(a.x).toInt() shr 4) == cx && (floor(a.z).toInt() shr 4) == cz) n++
        }
        return minOf(cap, n.toDouble() / saturate)
    }

    private fun retentionTicks(): Long {
        val cluster = GameplayConfig.ANTI_MANIP_CLUSTER_WINDOW_SECONDS.value.coerceAtLeast(1)
        val heat = GameplayConfig.ANTI_MANIP_HEAT_WINDOW_SECONDS.value.coerceAtLeast(1)
        return maxOf(cluster, heat) * 20L
    }

    private fun clusterSuppressed(log: ArrayDeque<Activity>, current: Activity): Boolean {
        val radius = GameplayConfig.ANTI_MANIP_CLUSTER_RADIUS_BLOCKS.value.toDouble()
        val threshold = GameplayConfig.ANTI_MANIP_CLUSTER_COUNT.value
        if (threshold <= 0) return false
        val windowTicks = GameplayConfig.ANTI_MANIP_CLUSTER_WINDOW_SECONDS.value.coerceAtLeast(1) * 20L
        val minTick = current.tick - windowTicks
        var count = 0
        for (a in log) {
            if (a.tick < minTick || a.eventType != current.eventType) continue
            if (squaredDistance(a, current) <= radius * radius) count++
        }
        return count >= threshold
    }

    private fun poseSuppressed(log: ArrayDeque<Activity>, current: Activity): Boolean {
        val k = GameplayConfig.ANTI_MANIP_POSE_COUNT.value
        if (k <= 0) return false
        val posTol = GameplayConfig.ANTI_MANIP_POSE_POSITION_BLOCKS.value
        val angTol = GameplayConfig.ANTI_MANIP_POSE_ANGLE_DEGREES.value
        val sameType = log.filter { it.eventType == current.eventType }
        if (sameType.size < k) return false
        val lastK = sameType.subList(sameType.size - k, sameType.size)
        val ref = lastK.first()
        for (a in lastK) {
            if (squaredDistance(a, ref) > posTol * posTol) return false
            if (abs(angleDiff(a.yaw, ref.yaw)) > angTol) return false
            if (abs(a.pitch - ref.pitch) > angTol) return false
            if (current.eventType == ActionEventType.READ && a.regionId != ref.regionId) return false
        }
        return true
    }

    private fun squaredDistance(a: Activity, b: Activity): Double {
        val dx = a.x - b.x
        val dy = a.y - b.y
        val dz = a.z - b.z
        return dx * dx + dy * dy + dz * dz
    }

    private fun angleDiff(a: Float, b: Float): Double {
        var d = (a - b).toDouble() % 360.0
        if (d < -180.0) d += 360.0
        if (d > 180.0) d -= 360.0
        return d
    }

    companion object {
        private const val MAX_TRACKED = 64
    }
}

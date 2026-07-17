package com.imyvm.adventure.entrypoint.api

import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.application.AdventureServices
import com.imyvm.adventure.domain.model.wilderness.Wilderness
import com.imyvm.adventure.domain.model.wilderness.WildernessSnapshot
import com.imyvm.adventure.domain.model.wilderness.WildernessStatus
import com.imyvm.adventure.domain.model.wilderness.toSnapshot
import com.imyvm.adventure.infra.WildernessDatabase

object WildernessApi {

    private const val MAX_NAME_LENGTH = 32

    fun create(regionNumberId: Int, name: String): Result<WildernessSnapshot> {
        requireServerThread()?.let { return Result.failure(it) }

        val validationError = validateCreateParameters(regionNumberId, name)
        if (validationError != null) {
            return Result.failure(IllegalArgumentException(validationError))
        }

        if (WildernessDatabase.getWildernessById(regionNumberId) != null) {
            return Result.failure(IllegalStateException("wilderness already exists for regionNumberId=$regionNumberId"))
        }

        if (!AdventureServices.worldGeoBridgeService.regionExists(regionNumberId)) {
            return Result.failure(IllegalStateException("region does not exist for regionNumberId=$regionNumberId"))
        }

        if (AdventureServices.communityBridgeService.isCommunityRegion(regionNumberId)) {
            return Result.failure(IllegalStateException("region $regionNumberId is a community and cannot be a wilderness"))
        }

        val wilderness = Wilderness(
            regionNumberId = regionNumberId,
            name = name.trim(),
            status = WildernessStatus.ACTIVE,
            creationTime = System.currentTimeMillis()
        )

        return runWildernessMutationOrRollback(
            mutate = {
                WildernessDatabase.addWilderness(wilderness)
                WildernessDatabase.save()
            },
            rollback = { WildernessDatabase.removeWilderness(wilderness) },
            onSuccess = { Result.success(wilderness.toSnapshot()) }
        )
    }

    fun update(
        regionNumberId: Int,
        name: String? = null,
        status: WildernessStatus? = null
    ): Result<WildernessSnapshot> {
        requireServerThread()?.let { return Result.failure(it) }

        val wilderness = WildernessDatabase.getWildernessById(regionNumberId)
            ?: return Result.failure(NoSuchElementException("wilderness not found for regionNumberId=$regionNumberId"))

        name?.let {
            val error = validateName(it)
            if (error != null) {
                return Result.failure(IllegalArgumentException(error))
            }
        }

        val previousName = wilderness.name
        val previousStatus = wilderness.status

        return runWildernessMutationOrRollback(
            mutate = {
                name?.let { wilderness.name = it.trim() }
                status?.let { wilderness.status = it }
                WildernessDatabase.save()
            },
            rollback = {
                wilderness.name = previousName
                wilderness.status = previousStatus
            },
            onSuccess = { Result.success(wilderness.toSnapshot()) }
        )
    }

    fun delete(regionNumberId: Int): Result<Unit> {
        requireServerThread()?.let { return Result.failure(it) }

        val wilderness = WildernessDatabase.getWildernessById(regionNumberId)
            ?: return Result.failure(NoSuchElementException("wilderness not found for regionNumberId=$regionNumberId"))

        val index = WildernessDatabase.wildernesses.indexOf(wilderness)
        if (index < 0) {
            return Result.failure(IllegalStateException("wilderness index inconsistency for regionNumberId=$regionNumberId"))
        }

        return runWildernessMutationOrRollback(
            mutate = {
                WildernessDatabase.removeWilderness(wilderness)
                WildernessDatabase.save()
            },
            rollback = { WildernessDatabase.wildernesses.add(index, wilderness) },
            onSuccess = { Result.success(Unit) }
        )
    }

    fun getByRegion(regionNumberId: Int): WildernessSnapshot? {
        return WildernessDatabase.getWildernessById(regionNumberId)?.toSnapshot()
    }

    fun list(): List<WildernessSnapshot> {
        return WildernessDatabase.wildernesses.map { it.toSnapshot() }
    }

    private fun validateCreateParameters(regionNumberId: Int, name: String): String? {
        if (regionNumberId <= 0) {
            return "regionNumberId must be positive"
        }
        return validateName(name)
    }

    private fun validateName(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            return "wilderness name must not be empty"
        }
        if (trimmed.length > MAX_NAME_LENGTH) {
            return "wilderness name must not exceed $MAX_NAME_LENGTH characters"
        }
        return null
    }

    private inline fun <T> runWildernessMutationOrRollback(
        mutate: () -> Unit,
        rollback: () -> Unit,
        onSuccess: () -> Result<T>
    ): Result<T> {
        return try {
            mutate()
            onSuccess()
        } catch (e: Exception) {
            try {
                rollback()
            } catch (rollbackError: Exception) {
                WorldGeoAdventureAddon.logger.error(
                    "Wilderness rollback failed after save error: ${rollbackError.message}",
                    rollbackError
                )
            }
            Result.failure(e)
        }
    }

    private fun requireServerThread(): IllegalStateException? {
        val server = WorldGeoAdventureAddon.server
            ?: return IllegalStateException("WildernessApi mutating calls require a running Minecraft server")
        if (!server.isSameThread) {
            return IllegalStateException("WildernessApi mutating calls must run on the Minecraft server thread")
        }
        return null
    }
}

package com.imyvm.adventure.infra

import com.google.gson.Gson
import com.imyvm.adventure.WorldGeoAdventureAddon
import com.imyvm.adventure.domain.model.AdventurePersistentState
import net.fabricmc.loader.api.FabricLoader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object AdventureDatabase {
    const val SCHEMA_VERSION = 1

    private const val DATABASE_MAGIC = 0x49574144
    private const val DATABASE_FILENAME = "iwg_adventure.db"
    private val gson = Gson()

    var state: AdventurePersistentState = AdventurePersistentState()
        private set

    internal var onSave: (() -> Unit)? = null

    @Throws(IOException::class)
    fun save() {
        val file = getDatabasePath()
        Files.createDirectories(file.parent)

        state.schemaVersion = SCHEMA_VERSION
        DataOutputStream(file.toFile().outputStream()).use { stream ->
            stream.writeInt(DATABASE_MAGIC)
            stream.writeInt(SCHEMA_VERSION)
            stream.writeUTF(gson.toJson(state))
        }
        onSave?.invoke()
    }

    @Throws(IOException::class)
    fun load() {
        val file = getDatabasePath()
        if (!file.toFile().exists()) {
            resetState()
            return
        }

        DataInputStream(file.toFile().inputStream()).use { stream ->
            val magic = stream.readInt()
            val schemaVersion = stream.readInt()
            if (magic != DATABASE_MAGIC) {
                WorldGeoAdventureAddon.logger.warn(
                    "Adventure database magic mismatch. Resetting in-memory state to schema {}.",
                    SCHEMA_VERSION
                )
                resetState()
                return
            }

            val encodedState = if (stream.available() > 0) stream.readUTF() else ""
            state = if (encodedState.isBlank()) {
                AdventurePersistentState()
            } else {
                gson.fromJson(encodedState, AdventurePersistentState::class.java) ?: AdventurePersistentState()
            }
            state.schemaVersion = schemaVersion
        }
    }

    private fun resetState() {
        state = AdventurePersistentState().also { it.schemaVersion = SCHEMA_VERSION }
    }

    private fun getDatabasePath(): Path =
        FabricLoader.getInstance().gameDir.resolve("world").resolve(DATABASE_FILENAME)
}

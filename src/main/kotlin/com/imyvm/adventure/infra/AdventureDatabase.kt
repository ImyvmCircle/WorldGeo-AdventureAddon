package com.imyvm.adventure.infra

import com.imyvm.adventure.WorldGeoAdventureAddon
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path

object AdventureDatabase {
    const val SCHEMA_VERSION = 1

    private const val DATABASE_MAGIC = 0x49574144
    private const val DATABASE_FILENAME = "iwg_adventure.db"

    var schemaVersion: Int = SCHEMA_VERSION
        private set

    @Throws(Exception::class)
    fun save(server: MinecraftServer? = WorldGeoAdventureAddon.server) {
        val file = getDatabasePath(server)
        Files.createDirectories(file.parent)

        val tempFile = Files.createTempFile(file.parent, "${DATABASE_FILENAME}.", ".tmp")
        try {
            DataOutputStream(Files.newOutputStream(tempFile)).use { stream ->
                stream.writeInt(DATABASE_MAGIC)
                stream.writeInt(SCHEMA_VERSION)
            }
            Files.move(tempFile, file, java.nio.file.StandardCopyOption.ATOMIC_MOVE, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        } catch (_: Exception) {
            Files.move(tempFile, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        } finally {
            Files.deleteIfExists(tempFile)
        }
    }

    @Throws(Exception::class)
    fun load(server: MinecraftServer? = WorldGeoAdventureAddon.server) {
        val file = getDatabasePath(server)
        if (!Files.exists(file)) {
            schemaVersion = SCHEMA_VERSION
            return
        }

        DataInputStream(Files.newInputStream(file)).use { stream ->
            val magic = stream.readInt()
            val loadedSchemaVersion = stream.readInt()
            if (magic != DATABASE_MAGIC) {
                WorldGeoAdventureAddon.logger.warn(
                    "Adventure database magic mismatch. Resetting in-memory schema to {}.",
                    SCHEMA_VERSION
                )
                schemaVersion = SCHEMA_VERSION
                return
            }
            schemaVersion = loadedSchemaVersion
        }
    }

    private fun getDatabasePath(server: MinecraftServer?): Path {
        if (server != null) {
            return server.getWorldPath(LevelResource.ROOT).resolve(DATABASE_FILENAME)
        }
        return FabricLoader.getInstance().gameDir.resolve("world").resolve(DATABASE_FILENAME)
    }
}

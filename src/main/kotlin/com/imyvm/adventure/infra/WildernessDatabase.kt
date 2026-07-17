package com.imyvm.adventure.infra

import com.imyvm.adventure.domain.model.wilderness.Wilderness
import com.imyvm.adventure.domain.model.wilderness.WildernessStatus
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.world.level.storage.LevelResource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object WildernessDatabase {

    private const val DATABASE_FILENAME = "iwg_wilderness.db"
    private const val DATABASE_VERSION_MARKER = -3
    private const val DATABASE_VERSION = 1
    private const val MAX_RECORD_BYTES = 16 * 1024 * 1024
    private const val MAX_WILDERNESSES = 100_000
    private var legacyDatabaseLoaded = false
    private var legacyBackupCreated = false

    lateinit var wildernesses: MutableList<Wilderness>

    @Throws(IOException::class)
    fun save() {
        val file = getDatabasePath()
        backupLegacyDatabaseBeforeSave(file)
        val parent = file.parent
        if (parent != null) Files.createDirectories(parent)
        val tempFile = Files.createTempFile(parent, "${DATABASE_FILENAME}.", ".tmp")
        try {
            DataOutputStream(Files.newOutputStream(tempFile)).use { stream ->
                stream.writeInt(DATABASE_VERSION_MARKER)
                stream.writeInt(DATABASE_VERSION)
                stream.writeInt(wildernesses.size)
                for (w in wildernesses) {
                    writeRecord(stream, w)
                }
            }
            Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        } catch (_: IOException) {
            Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING)
        } finally {
            Files.deleteIfExists(tempFile)
        }
    }

    private fun writeRecord(stream: DataOutputStream, wilderness: Wilderness) {
        val buffer = ByteArrayOutputStream()
        DataOutputStream(buffer).use { wStream ->
            wStream.writeInt(wilderness.regionNumberId)
            wStream.writeUTF(wilderness.name)
            wStream.writeInt(wilderness.status.value)
            wStream.writeLong(wilderness.creationTime)
        }
        val payload = buffer.toByteArray()
        stream.writeInt(payload.size)
        stream.write(payload)
    }

    fun load() {
        loadDatabaseFile(getDatabasePath())
    }

    fun load(server: net.minecraft.server.MinecraftServer) {
        loadDatabaseFile(getDatabasePath(server))
    }

    private fun loadDatabaseFile(file: Path) {
        val previousWildernesses = if (this::wildernesses.isInitialized) wildernesses else null
        legacyDatabaseLoaded = false
        legacyBackupCreated = false
        try {
            if (!file.toFile().exists()) {
                wildernesses = mutableListOf()
                return
            }
            DataInputStream(file.toFile().inputStream()).use { stream ->
                val firstInt = stream.readInt()
                val databaseVersion = if (firstInt == DATABASE_VERSION_MARKER) stream.readInt() else 1
                require(databaseVersion in 1..DATABASE_VERSION) { "Unsupported wilderness database version: $databaseVersion" }
                legacyDatabaseLoaded = databaseVersion < DATABASE_VERSION
                if (legacyDatabaseLoaded) backupLegacyDatabaseBeforeLoad(file)
                val size = requireCount(
                    if (databaseVersion == 1) firstInt else stream.readInt(),
                    "wilderness",
                    MAX_WILDERNESSES
                )
                val loaded = ArrayList<Wilderness>(size)
                for (i in 0 until size) {
                    loaded.add(loadRecord(stream, databaseVersion))
                }
                wildernesses = loaded
            }
        } catch (e: Exception) {
            if (previousWildernesses != null) wildernesses = previousWildernesses
            throw e
        }
    }

    private fun loadRecord(stream: DataInputStream, databaseVersion: Int): Wilderness {
        val length = stream.readInt()
        require(length in 0..MAX_RECORD_BYTES) { "Invalid wilderness record length: $length" }
        val payload = stream.readNBytes(length)
        require(payload.size == length) { "Truncated wilderness record" }
        DataInputStream(ByteArrayInputStream(payload)).use { wStream ->
            val w = loadRecordBody(wStream)
            require(wStream.available() == 0) { "Unread bytes in wilderness record" }
            return w
        }
    }

    private fun loadRecordBody(stream: DataInputStream): Wilderness {
        val regionNumberId = stream.readInt()
        val name = stream.readUTF()
        val status = WildernessStatus.fromValue(stream.readInt())
        val creationTime = stream.readLong()
        return Wilderness(
            regionNumberId = regionNumberId,
            name = name,
            status = status,
            creationTime = creationTime
        )
    }

    fun addWilderness(wilderness: Wilderness) {
        wildernesses.add(wilderness)
    }

    fun removeWilderness(wilderness: Wilderness) {
        wildernesses.remove(wilderness)
    }

    fun getWildernessById(regionNumberId: Int): Wilderness? {
        return wildernesses.find { it.regionNumberId == regionNumberId }
    }

    fun backupDatabaseAfterLoadFailure(): Path? {
        return backupDatabaseFile(getDatabasePath(), "corrupt", System.currentTimeMillis())
    }

    private fun backupLegacyDatabaseBeforeLoad(databaseFile: Path): Path? {
        val backupFile = backupDatabaseFile(databaseFile, "legacy", System.currentTimeMillis())
        legacyBackupCreated = backupFile != null
        return backupFile
    }

    private fun backupLegacyDatabaseBeforeSave(databaseFile: Path): Path? {
        if (!legacyDatabaseLoaded || legacyBackupCreated) return null
        val backupFile = backupDatabaseFile(databaseFile, "legacy", System.currentTimeMillis())
        legacyBackupCreated = backupFile != null
        return backupFile
    }

    private fun backupDatabaseFile(databaseFile: Path, label: String, timestamp: Long): Path? {
        if (!Files.exists(databaseFile)) return null
        val backupBase = databaseFile.resolveSibling("${databaseFile.fileName}.$label.$timestamp")
        var backupFile = backupBase
        var suffix = 1
        while (Files.exists(backupFile)) {
            backupFile = databaseFile.resolveSibling("${backupBase.fileName}.$suffix")
            suffix++
        }
        return Files.copy(databaseFile, backupFile)
    }

    private fun getDatabasePath(): Path {
        return getDatabasePath(null)
    }

    private fun getDatabasePath(server: net.minecraft.server.MinecraftServer?): Path {
        if (server != null) {
            return server.getWorldPath(LevelResource.ROOT).resolve(DATABASE_FILENAME)
        }
        return FabricLoader.getInstance().gameDir
            .resolve("world")
            .resolve(DATABASE_FILENAME)
    }

    private fun requireCount(count: Int, label: String, max: Int): Int {
        require(count in 0..max) { "Invalid $label count: $count" }
        return count
    }
}

package com.emberline.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class LoaderKind {
    VANILLA,
    FABRIC,
    NEOFORGE,
    FORGE
}

@Serializable
data class InstanceConfig(
    val id: String,
    val name: String,
    val minecraftVersion: String,
    val loader: LoaderKind = LoaderKind.VANILLA,
    val javaRuntimeId: String? = null,
    val minMemoryMb: Int = DEFAULT_MIN_MEMORY_MB,
    val maxMemoryMb: Int = DEFAULT_MAX_MEMORY_MB,
    val rendererId: String = "auto",
    val createdAtEpochMs: Long,
    val lastPlayedEpochMs: Long? = null,
    val schemaVersion: Int = SCHEMA_VERSION
) {
    init {
        require(id.isNotBlank()) { "instance id must not be blank" }
        require(name.isNotBlank()) { "instance name must not be blank" }
        require(minecraftVersion.isNotBlank()) { "minecraft version must not be blank" }
        require(minMemoryMb in MIN_MEMORY_MB..maxMemoryMb) {
            "min memory must be within [$MIN_MEMORY_MB, $maxMemoryMb]"
        }
        require(maxMemoryMb <= MAX_MEMORY_MB) { "max memory must not exceed $MAX_MEMORY_MB MB" }
    }

    fun markPlayed(epochMs: Long): InstanceConfig = copy(lastPlayedEpochMs = epochMs)

    companion object {
        const val SCHEMA_VERSION = 1
        const val MIN_MEMORY_MB = 1024
        const val MAX_MEMORY_MB = 32768
        const val DEFAULT_MIN_MEMORY_MB = 2048
        const val DEFAULT_MAX_MEMORY_MB = 4096
    }
}

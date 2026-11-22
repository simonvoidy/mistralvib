package com.emberline.core.model

import kotlinx.serialization.Serializable

@Serializable
data class VersionManifest(
    val latest: LatestVersions,
    val versions: List<VersionEntry>
) {
    fun find(id: String): VersionEntry? = versions.firstOrNull { it.id == id }
}

@Serializable
data class LatestVersions(
    val release: String,
    val snapshot: String
)

@Serializable
data class VersionEntry(
    val id: String,
    val type: String,
    val url: String,
    val time: String,
    val releaseTime: String,
    val sha1: String? = null,
    val complianceLevel: Int = 0
)

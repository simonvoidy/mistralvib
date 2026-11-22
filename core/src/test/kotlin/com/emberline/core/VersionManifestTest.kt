package com.emberline.core

import com.emberline.core.io.JsonStore
import com.emberline.core.model.VersionManifest
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class VersionManifestTest {

    private val sample = """
        {
          "latest": {"release": "1.21.1", "snapshot": "1.21.3"},
          "versions": [
            {"id": "1.21.1", "type": "release", "url": "https://piston-meta.mojang.com/v1/packages/a/1.21.1.json",
             "time": "2024-10-02T14:39:11+00:00", "releaseTime": "2024-10-02T14:39:11+00:00",
             "sha1": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "complianceLevel": 1},
            {"id": "1.21.3", "type": "snapshot", "url": "https://piston-meta.mojang.com/v1/packages/b/1.21.3.json",
             "time": "2024-10-16T09:15:00+00:00", "releaseTime": "2024-10-16T09:15:00+00:00"}
          ]
        }
    """.trimIndent()

    private val store = JsonStore()

    @Test
    fun parsesManifestAndLocatesVersions() {
        val manifest = store.decode(sample, VersionManifest.serializer())
        assertNotNull(manifest)
        assertEquals("1.21.1", manifest!!.latest.release)
        assertEquals(2, manifest.versions.size)
        assertEquals("1.21.3", manifest.find("1.21.3")!!.id)
        assertNull(manifest.find("1.20"))
    }

    @Test
    fun ignoresUnknownFieldsForForwardCompatibility() {
        val extended = sample.replace(
            "\"complianceLevel\": 1",
            "\"complianceLevel\": 1, \"futureField\": {}"
        )
        val manifest = store.decode(extended, VersionManifest.serializer())
        assertNotNull(manifest)
    }

    @Test
    fun stringRoundTrip() {
        val manifest = store.decode(sample, VersionManifest.serializer())!!
        val out = store.encode(manifest, VersionManifest.serializer())
        assertEquals(manifest, store.decode(out, VersionManifest.serializer()))
    }

    @Test
    fun fileRoundTrip() {
        val dir = Files.createTempDirectory("emberline-test")
        val path = dir.resolve("manifest.json")
        val manifest = store.decode(sample, VersionManifest.serializer())!!
        store.write(path, manifest, VersionManifest.serializer())
        assertEquals(manifest, store.read(path, VersionManifest.serializer()))
    }
}

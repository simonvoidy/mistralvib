package com.emberline.core

import com.emberline.core.io.JsonStore
import com.emberline.core.model.InstanceConfig
import com.emberline.core.model.LoaderKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class InstanceConfigTest {

    private val store = JsonStore()

    @Test
    fun roundTripsThroughJson() {
        val config = InstanceConfig(
            id = "abc123",
            name = "Survival",
            minecraftVersion = "1.21.1",
            loader = LoaderKind.FABRIC,
            createdAtEpochMs = 1_700_000_000_000
        )
        val json = store.encode(config)
        val restored = store.decode(json)
        assertEquals(config, restored)
    }

    @Test
    fun rejectsBlankName() {
        assertThrows(IllegalArgumentException::class.java) {
            InstanceConfig(id = "a", name = " ", minecraftVersion = "1.21.1", createdAtEpochMs = 0)
        }
    }

    @Test
    fun rejectsMemoryBelowFloor() {
        assertThrows(IllegalArgumentException::class.java) {
            InstanceConfig(
                id = "a", name = "x", minecraftVersion = "1.21.1",
                minMemoryMb = 0, createdAtEpochMs = 0
            )
        }
    }

    @Test
    fun rejectsMaxAboveCeiling() {
        assertThrows(IllegalArgumentException::class.java) {
            InstanceConfig(
                id = "a", name = "x", minecraftVersion = "1.21.1",
                maxMemoryMb = 999_999, createdAtEpochMs = 0
            )
        }
    }

    private fun JsonStore.encode(config: InstanceConfig): String =
        encode(config, InstanceConfig.serializer())

    private fun JsonStore.decode(source: String): InstanceConfig =
        decode(source, InstanceConfig.serializer())!!
}

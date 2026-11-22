package com.emberline.core

import com.emberline.core.interop.ZalithLayoutImport
import com.emberline.core.io.JsonStore
import com.emberline.core.model.ControlElement
import com.emberline.core.model.ControlKind
import com.emberline.core.model.ControlProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ControlProfileTest {

    private val store = JsonStore()

    @Test
    fun roundTripsThroughJson() {
        val profile = ControlProfile(
            id = "default",
            name = "Default",
            elements = listOf(
                ControlElement("j1", ControlKind.JOYSTICK, "Move", 0.1f, 0.8f),
                ControlElement("b1", ControlKind.BUTTON, "Jump", 0.9f, 0.8f, mapping = "key.space")
            )
        )
        val json = store.encode(profile)
        assertEquals(profile, store.decode(json))
    }

    @Test
    fun rejectsOutOfBoundsAnchors() {
        assertThrows(IllegalArgumentException::class.java) {
            ControlElement("b", ControlKind.BUTTON, anchorX = 1.5f)
        }
    }

    @Test
    fun importMapsGenericZalithStyleLayout() {
        val source = """
            {
              "modifiedCustomJoystick": false,
              "control": [
                {"name": "Forward", "key": "key.forward", "x": 25, "y": 80, "size": 70, "opacity": 0.8},
                {"name": "Jump", "key": "key.space", "x": 90, "y": 75, "size": 55, "opacity": 0.6},
                {"unknownOnly": true}
              ]
            }
        """.trimIndent()

        assertTrue(ZalithLayoutImport.accepts(source))
        val report = ZalithLayoutImport.toProfile(source, name = "My layout")

        assertEquals(2, report.imported)
        assertEquals(1, report.skipped.size)
        assertEquals("key.forward", report.profile.elements[0].mapping)
        assertEquals(0.25f, report.profile.elements[0].anchorX, 0.0001f)
        assertEquals(0.8f, report.profile.elements[0].anchorY, 0.0001f)
        assertEquals(70, report.profile.elements[0].sizePx)
        assertEquals("My layout", report.profile.name)
    }

    @Test
    fun importOfEmptyLayoutYieldsEmptyProfile() {
        val report = ZalithLayoutImport.toProfile("""{"control": []}""")
        assertEquals(0, report.profile.elements.size)
        assertTrue(report.skipped.isEmpty())
    }
}

private fun JsonStore.encode(profile: ControlProfile): String =
    encode(profile, ControlProfile.serializer())

private fun JsonStore.decode(source: String): ControlProfile =
    decode(source, ControlProfile.serializer())!!

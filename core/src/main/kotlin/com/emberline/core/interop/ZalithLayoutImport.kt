package com.emberline.core.interop

import com.emberline.core.model.ControlElement
import com.emberline.core.model.ControlKind
import com.emberline.core.model.ControlProfile
import java.util.UUID
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

data class ImportReport(
    val profile: ControlProfile,
    val imported: Int,
    val skipped: List<String>
)

object ZalithLayoutImport {

    private val json = Json { ignoreUnknownKeys = true }

    fun accepts(source: String): Boolean = runCatching {
        (json.parseToJsonElement(source) as? JsonObject)?.containsKey("control") == true
    }.getOrDefault(false)

    fun toProfile(source: String, name: String = "imported"): ImportReport {
        val root = json.parseToJsonElement(source) as? JsonObject
            ?: error("not a Zalith-style layout document")
        val array: JsonArray = root["control"]?.jsonArray ?: JsonArray(emptyList())
        val skipped = mutableListOf<String>()
        val elements = array.mapIndexedNotNull { index, item ->
            val entry = item as? JsonObject
            if (entry == null) {
                skipped.add("item $index is not an object")
                return@mapIndexedNotNull null
            }
            mapElement(index, entry, skipped)
        }
        val profile = ControlProfile(
            id = "imported-" + UUID.randomUUID(),
            name = name,
            elements = elements.take(ControlProfile.MAX_ELEMENTS)
        )
        return ImportReport(profile, elements.size, skipped)
    }

    private fun mapElement(index: Int, entry: JsonObject, skipped: MutableList<String>): ControlElement? {
        val mapping = entry.string("key")
        val label = entry.string("name").orEmpty()
        val x = entry.float("x")
        val y = entry.float("y")
        if (mapping.isNullOrBlank() || x == null || y == null) {
            skipped.add("item $index missing key/position")
            return null
        }
        val kind = if (entry.boolean("special") == true) ControlKind.TOGGLE else ControlKind.BUTTON
        return ControlElement(
            id = "zalith-$index",
            kind = kind,
            label = label,
            anchorX = (x / 100f).coerceIn(0f, 1f),
            anchorY = (y / 100f).coerceIn(0f, 1f),
            sizePx = entry.float("size")?.toInt()?.coerceIn(
                ControlElement.MIN_SIZE,
                ControlElement.MAX_SIZE
            ) ?: ControlElement.DEFAULT_SIZE,
            opacity = entry.float("opacity")?.coerceIn(0.05f, 1f) ?: 0.5f,
            mapping = mapping
        )
    }

    private fun JsonObject.string(name: String): String? =
        (this[name] as? JsonPrimitive)?.content

    private fun JsonObject.float(name: String): Float? =
        (this[name] as? JsonPrimitive)?.floatOrNull

    private fun JsonObject.boolean(name: String): Boolean? =
        (this[name] as? JsonPrimitive)?.let {
            runCatching { it.jsonPrimitive.content.toBooleanStrictOrNull() }.getOrNull()
        }
}

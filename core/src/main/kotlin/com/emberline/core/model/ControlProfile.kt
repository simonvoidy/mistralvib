package com.emberline.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ControlKind {
    JOYSTICK,
    BUTTON,
    DPAD,
    TOGGLE
}

@Serializable
data class ControlProfile(
    val id: String,
    val name: String,
    val schema: Int = SCHEMA_VERSION,
    val elements: List<ControlElement>
) {
    init {
        require(elements.size <= MAX_ELEMENTS) { "too many control elements" }
        elements.forEach { it.validate() }
    }

    fun find(id: String): ControlElement? = elements.firstOrNull { it.id == id }

    companion object {
        const val SCHEMA_VERSION = 1
        const val MAX_ELEMENTS = 64
    }
}

@Serializable
data class ControlElement(
    val id: String,
    val kind: ControlKind,
    val label: String = "",
    val anchorX: Float = 0f,
    val anchorY: Float = 0f,
    val sizePx: Int = DEFAULT_SIZE,
    val opacity: Float = 0.5f,
    val mapping: String = ""
) {
    init {
        require(id.isNotBlank()) { "element id must not be blank" }
        require(anchorX in 0f..1f && anchorY in 0f..1f) { "anchor must be normalized" }
        require(sizePx in MIN_SIZE..MAX_SIZE) { "size out of range" }
        require(opacity in 0.05f..1f) { "opacity out of range" }
    }

    fun validate() {}

    fun withAnchor(x: Float, y: Float): ControlElement = copy(
        anchorX = x.coerceIn(0f, 1f),
        anchorY = y.coerceIn(0f, 1f)
    )

    companion object {
        const val DEFAULT_SIZE = 64
        const val MIN_SIZE = 24
        const val MAX_SIZE = 256
    }
}

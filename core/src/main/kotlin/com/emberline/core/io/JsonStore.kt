package com.emberline.core.io

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class JsonStore(private val json: Json = defaultJson()) {

    fun <T> write(path: Path, value: T, serializer: KSerializer<T>) {
        val encoded = json.encodeToString(serializer, value)
        try {
            val parent = path.parent
            if (parent != null) {
                Files.createDirectories(parent)
            }
            val temp = path.resolveSibling(path.fileName.toString() + ".tmp")
            Files.writeString(temp, encoded)
            try {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            } catch (e: IOException) {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (e: IOException) {
            throw IOException("failed to write ${path.fileName}", e)
        }
    }

    fun <T> read(path: Path, serializer: KSerializer<T>): T? {
        if (!Files.exists(path)) {
            return null
        }
        val source = try {
            Files.readString(path)
        } catch (e: IOException) {
            return null
        }
        return decode(source, serializer)
    }

    fun <T> decode(source: String, serializer: KSerializer<T>): T? =
        try {
            json.decodeFromString(serializer, source)
        } catch (e: Exception) {
            null
        }

    fun <T> encode(value: T, serializer: KSerializer<T>): String =
        json.encodeToString(serializer, value)

    companion object {
        fun defaultJson(): Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
}

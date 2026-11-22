package com.emberline.launcher.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emberline.core.io.JsonStore
import com.emberline.core.model.InstanceConfig
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    private val store = JsonStore()
    private val instancesDir = File(
        app.getExternalFilesDir(null) ?: app.filesDir,
        "instances"
    )

    private val _instances = MutableStateFlow(loadInstances())
    val instances: StateFlow<List<InstanceConfig>> = _instances.asStateFlow()

    fun addInstance(name: String, minecraftVersion: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val config = InstanceConfig(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                minecraftVersion = minecraftVersion.trim(),
                createdAtEpochMs = System.currentTimeMillis()
            )
            val dir = File(instancesDir, config.id)
            dir.mkdirs()
            store.write(File(dir, "instance.json").toPath(), config, InstanceConfig.serializer())
            _instances.value = loadInstances()
        }
    }

    fun deleteInstance(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            File(instancesDir, id).deleteRecursively()
            _instances.value = loadInstances()
        }
    }

    private fun loadInstances(): List<InstanceConfig> =
        instancesDir.listFiles { file -> file.isDirectory }
            .orEmpty()
            .mapNotNull { dir ->
                store.read(File(dir, "instance.json").toPath(), InstanceConfig.serializer())
            }
            .sortedBy { it.name.lowercase() }
}

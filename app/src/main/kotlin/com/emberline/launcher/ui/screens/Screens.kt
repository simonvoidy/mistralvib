package com.emberline.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emberline.core.model.InstanceConfig

@Composable
private fun Shell(title: String, subtitle: String, content: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun StatusCard(headline: String, detail: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(headline, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(detail, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun HomeScreen(instances: List<InstanceConfig>) {
    Shell(
        title = "Emberline",
        subtitle = "An original Minecraft: Java Edition launcher for Android. Unofficial — not affiliated with Mojang or Microsoft."
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                StatusCard(
                    "Instances",
                    if (instances.isEmpty()) "No instances yet — create one under Instances." else instances.size.toString() + " instance(s) on this device."
                )
            }
            item { StatusCard("Game runtime", "Planned (M6). The launcher cannot boot the game yet.") }
            item { StatusCard("Accounts", "Planned (M5). Microsoft sign-in arrives with M5.") }
            item { StatusCard("Controls", "Planned (M7). Touch control editor is under construction.") }
            instances.forEach { instance ->
                item {
                    StatusCard(instance.name, "Minecraft " + instance.minecraftVersion + " · " + instance.loader.name.lowercase())
                }
            }
        }
    }
}

@Composable
fun InstancesScreen(
    instances: List<InstanceConfig>,
    onAdd: (String, String) -> Unit,
    onDelete: (String) -> Unit
) {
    var showAdd by remember { mutableStateOf(false) }

    Shell(title = "Instances", subtitle = "Each instance keeps an isolated game directory and configuration.") {
        Button(onClick = { showAdd = true }) {
            Text("New instance")
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(instances, key = { it.id }) { instance ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(instance.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Minecraft " + instance.minecraftVersion + " · " + instance.loader.name.lowercase(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        TextButton(onClick = { onDelete(instance.id) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        var name by remember { mutableStateOf("") }
        var version by remember { mutableStateOf("1.21.1") }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("New instance") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = version,
                        onValueChange = { version = it },
                        label = { Text("Minecraft version") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isNotBlank() && version.isNotBlank()) {
                            onAdd(name, version)
                        }
                        showAdd = false
                    }
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showAdd = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AccountsScreen() {
    Shell(
        title = "Accounts",
        subtitle = "Planned (M5): Microsoft account sign-in via the official device-code flow, with offline accounts as a clearly labeled fallback."
    )
}

@Composable
fun JavaScreen() {
    Shell(
        title = "Java runtimes",
        subtitle = "Planned, experimental (M4): per-instance Java runtime selection and memory allocation. Runtime packages are user-imported."
    )
}

@Composable
fun ControlsScreen() {
    Shell(
        title = "Touch controls",
        subtitle = "Planned (M7): joystick, buttons, per-profile position, size and opacity, drag & drop editing, multi-touch, import/export of control layouts."
    )
}

@Composable
fun SettingsScreen() {
    Shell(
        title = "Settings",
        subtitle = "Planned (M2): storage root, download behavior, and renderer defaults."
    )
}

package com.emberline.launcher.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emberline.launcher.data.LauncherViewModel
import com.emberline.launcher.ui.screens.AccountsScreen
import com.emberline.launcher.ui.screens.ControlsScreen
import com.emberline.launcher.ui.screens.HomeScreen
import com.emberline.launcher.ui.screens.InstancesScreen
import com.emberline.launcher.ui.screens.JavaScreen
import com.emberline.launcher.ui.screens.SettingsScreen

private object Routes {
    const val HOME = "home"
    const val INSTANCES = "instances"
    const val ACCOUNTS = "accounts"
    const val JAVA = "java"
    const val CONTROLS = "controls"
    const val SETTINGS = "settings"
}

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem(Routes.HOME, "Home", Icons.Filled.Home),
    NavItem(Routes.INSTANCES, "Instances", Icons.Filled.PlayArrow),
    NavItem(Routes.ACCOUNTS, "Accounts", Icons.Filled.Person),
    NavItem(Routes.JAVA, "Java", Icons.Filled.Build),
    NavItem(Routes.CONTROLS, "Controls", Icons.Filled.List),
    NavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings)
)

@Composable
fun EmberlineApp() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val viewModel: LauncherViewModel = viewModel()
    val instances by viewModel.instances.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(instances = instances)
            }
            composable(Routes.INSTANCES) {
                InstancesScreen(
                    instances = instances,
                    onAdd = viewModel::addInstance,
                    onDelete = viewModel::deleteInstance
                )
            }
            composable(Routes.ACCOUNTS) { AccountsScreen() }
            composable(Routes.JAVA) { JavaScreen() }
            composable(Routes.CONTROLS) { ControlsScreen() }
            composable(Routes.SETTINGS) { SettingsScreen() }
        }
    }
}

package org.dedda.copycat.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.util.Consumer
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.dedda.copycat.android.sampledata.SampleRepository
import org.dedda.copycat.database.DatabaseDriverFactory
import org.dedda.copycat.database.DatabaseRepository
import org.dedda.copycat.database.Repository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = DatabaseRepository(DatabaseDriverFactory(this))
        if (database.allServers().isEmpty()) {
            SampleRepository().allServers().forEach {
                database.insertServer(it.name, it.address)
            }
        }
        setContent {
            val navController = rememberNavController()
            MyApplicationTheme {
                Scaffold(
                    topBar = {},
                    content = { padding ->
                        NavHost(
                            modifier = Modifier.padding(padding),
                            navController = navController,
                            startDestination = "home",
                        ) {
                            composable("home") {
                                HomeContents(database)
                            }
                            composable("servers") {
                                ServerListContents(
                                    database,
                                    onNavigateToAddServer = { navController.navigate("addServer") },
                                    onNavigateToEditServer = { serverId -> navController.navigate("editServer/$serverId") },
                                )
                            }
                            composable("addServer") {
                                AddServerContents(
                                    database,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                "addServer/{address}",
                                arguments = listOf(navArgument("address") {
                                    type = NavType.StringType
                                    nullable = true
                                })
                            ) { backStackEntry ->
                                AddServerContents(
                                    database,
                                    startAddress = backStackEntry.arguments?.getString("address")
                                        ?: "",
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                "editServer/{serverId}",
                                arguments = listOf(navArgument("serverId") {
                                    type = NavType.LongType
                                    nullable = false
                                })
                            ) { backStackEntry ->
                                EditServer(
                                    database,
                                    serverId = backStackEntry.arguments!!.getLong("serverId"),
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("settings") {
                                SettingsContents()
                            }
                        }
                        (LocalContext.current as? Activity)?.intent?.let {
                            handleIntentUriNavigation(it, navController, database)
                        }
                    },
                    bottomBar = {
                        BottomNavigation {
                            val navBackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackEntry?.destination?.route
                            BottomNavItem.values().forEach { navItem ->
                                BottomNavigationItem(
                                    selected = currentRoute == navItem.route,
                                    onClick = {
                                        navController.backQueue.clear()
                                        navController.navigate(navItem.route)
                                    },
                                    icon = {
                                        Icon(
                                            navItem.icon,
                                            navItem.contentDescription,
                                            tint = appColors().navBarLabelColor
                                        )
                                    },
                                    label = {
                                        Text(
                                            color = appColors().navBarLabelColor,
                                            text = stringResource(navItem.label),
                                        )
                                    },
                                )
                            }
                        }
                    }
                )
            }
            DisposableEffect(Unit) {
                val listener = Consumer<Intent> {
                    handleIntentUriNavigation(intent, navController, database)
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }
        }
    }
}

private fun handleIntentUriNavigation(
    intent: Intent,
    navController: NavController,
    repo: Repository,
) {
    val serverAddress = addServerAddress(intent)
    serverAddress?.let { addServerAddress ->
        navigateToServerConfiguration(repo, addServerAddress, navController)
    }
}

private fun navigateToServerConfiguration(
    repo: Repository,
    addServerAddress: String,
    navController: NavController
) {
    val foundServer = repo.serverByAddress(addServerAddress)
    if (foundServer != null) {
        navController.navigate("editServer/${foundServer.id}")
    } else {
        navController.navigate("addServer/$addServerAddress")
    }
}

private fun addServerAddress(intent: Intent): String? {
    if (intent.action == Intent.ACTION_VIEW) {
        val data = intent.data ?: return null
        if (data.scheme == "copycat" && data.host == "connect.app") {
            return data.getQueryParameter("address")
        }
    }
    return null
}

private enum class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val contentDescription: String,
    val label: Int
) {
    HomeNavItem("home", Icons.Filled.Home, "Home icon", R.string.nav_home_label),
    ServerListNavItem("servers", Icons.Filled.List, "Server list icon", R.string.nav_servers_label),
    SettingsNavItem("settings", Icons.Filled.Settings, "Settings icon", R.string.nav_settings_label)
}
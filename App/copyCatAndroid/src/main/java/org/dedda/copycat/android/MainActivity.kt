package org.dedda.copycat.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
            MyApplicationTheme {
                MainContents()
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainContents(
    repo: Repository = DatabaseRepository(DatabaseDriverFactory(LocalContext.current))
) {
    val navControler = rememberNavController()
    Scaffold(
        topBar = {},
        content = { padding ->
            NavHost(
                modifier = Modifier.padding(padding),
                navController = navControler,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeContents(repo)
                }
                composable("servers") {
                    ServerListContents(
                        repo,
                        onNavigateToAddServer = { navControler.navigate("addServer") })
                }
                composable("addServer") {
                    AddServerContents(
                        repo,
                        onNavigateBack = { navControler.popBackStack() }
                    )
                }
                composable(
                    "editServer/{serverId}",
                    arguments = listOf(navArgument("serverId") { type = NavType.LongType })
                ) { backStackEntry ->
                    EditServer(repo, serverId = backStackEntry.arguments!!.getLong("serverId"))
                }
            }
        },
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(selected = true,
                    onClick = {
                        navControler.backQueue.clear()
                        navControler.navigate("home")
                    },
                    icon = {

                    },
                    label = {
                        Text(text = "Home")
                    })
                BottomNavigationItem(selected = false,
                    onClick = {
                        navControler.backQueue.clear()
                        navControler.navigate("servers")
                    },
                    icon = {

                    },
                    label = {
                        Text(text = "Servers")
                    })
            }
        }
    )
}

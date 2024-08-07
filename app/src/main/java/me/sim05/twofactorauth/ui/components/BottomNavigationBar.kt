package me.sim05.twofactorauth.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.sim05.twofactorauth.Pages
import me.sim05.twofactorauth.ui.theme.TwoFactorAuthTheme

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem(Pages.Home.name, Icons.Default.Home, "Home")
    data object Settings : BottomNavItem(Pages.Settings.name, Icons.Default.Settings, "Settings")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val pages = listOf(
        BottomNavItem.Home,
        BottomNavItem.Settings,
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route


        pages.forEach { page ->
            NavigationBarItem(
                label = {
                    Text(page.label)
                },
                icon = {
                    Icon(page.icon, null)
                },
                selected = currentRoute == page.route,
                onClick = {
                    navController.navigate(page.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = Color.Gray,
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.surfaceDim
                ),
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    TwoFactorAuthTheme {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = rememberNavController())
            }
        ) { innerPadding ->
            Text("placeholder", modifier = Modifier.padding(innerPadding))
        }
    }
}
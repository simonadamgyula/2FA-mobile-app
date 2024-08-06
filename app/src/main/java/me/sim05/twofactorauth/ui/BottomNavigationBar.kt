package me.sim05.twofactorauth.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import me.sim05.twofactorauth.Pages
import me.sim05.twofactorauth.ServicesViewModel

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
        val navBackStackEntry by navController.currentBackStackEntryAsState();
        val currentRoute = navBackStackEntry?.destination?.route;


        pages.forEach { page ->
            NavigationBarItem(
                label = {
                    Text(page.label);
                },
                icon = {
                    Icon(page.icon, null);
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
                    unselectedTextColor = Color.Gray,
                    selectedTextColor = Color.Black,
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Black,
                    indicatorColor = Color.White
                ),
            );
        };
    }
}
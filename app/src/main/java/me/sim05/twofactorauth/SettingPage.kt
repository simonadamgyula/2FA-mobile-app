package me.sim05.twofactorauth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.sim05.twofactorauth.ui.components.BottomNavigationBar

data class Setting(val title: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val settings: List<Setting> = listOf(
        Setting("List style", Icons.Default.Info, onClick = {}),
        Setting("Theme", Icons.AutoMirrored.Filled.List, onClick = {}),
        Setting("Show next token", Icons.Default.Info, onClick = {}),
        Setting("Hidden tokens", Icons.Default.Info, onClick = {}),
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Text("This is the settings page", modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun SettingsList(modifier: Modifier = Modifier, settings: List<Setting>) {
    Column(modifier = modifier) {
        settings.forEach {setting ->
            SettingElement(
                title = setting.title,
                icon = setting.icon,
                onClick = setting.onClick
            )
        }
    }
}

@Composable
fun SettingElement(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.padding(16.dp))
        Text(title)
    }
}
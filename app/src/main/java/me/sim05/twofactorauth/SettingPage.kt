package me.sim05.twofactorauth

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import me.sim05.twofactorauth.ui.components.BottomNavigationBar

@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Text("This is the settings page", modifier = Modifier.padding(innerPadding))
    }
}
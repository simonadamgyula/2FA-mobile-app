package me.sim05.twofactorauth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.sim05.twofactorauth.ui.components.BottomNavigationBar

@Composable
fun AboutPage(navController: NavController) {
    Scaffold(bottomBar = {
        BottomNavigationBar(navController)
    }) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding).padding(16.dp)) {
            Text("AuthApp by Sim05", style = MaterialTheme.typography.titleLarge)
            Text("Wanted to make the best 2FA app, but my skills betrayed me.")
        }
    }
}
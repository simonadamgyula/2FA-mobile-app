package me.sim05.twofactorauth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import me.sim05.twofactorauth.ui.components.BottomNavigationBar

@Composable
fun AboutPage(navController: NavController) {
    Scaffold(bottomBar = {
        BottomNavigationBar(navController)
    }) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) { Text("This is the about page") }
    }
}
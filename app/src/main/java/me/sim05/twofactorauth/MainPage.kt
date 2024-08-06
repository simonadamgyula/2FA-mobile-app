package me.sim05.twofactorauth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import me.sim05.twofactorauth.ui.BottomNavigationBar

@Composable
fun MainPage(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Pages.Add.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            ) {
                Icon(Icons.Filled.Add, "Add new 2FA service")
            }
        },
        content = { innerPadding ->
            TwoFactorAuthServices(modifier = modifier.padding(innerPadding).padding(top = 20.dp))
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    )
}

@Composable
fun TwoFactorAuthServices(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        TwoFactorAuthService()
        TwoFactorAuthService()
    }
}

@Composable
fun TwoFactorAuthService(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Icon(
            Icons.Filled.Info,
            "Service icon/logo",
            modifier = Modifier
                .padding(end = 10.dp)
                .size(50.dp)
        )
        Column {
            Text(
                "Service name",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                "Username connected to token",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "000 000",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.Filled.CheckCircle,
            "placeholder"
        )
    }
}
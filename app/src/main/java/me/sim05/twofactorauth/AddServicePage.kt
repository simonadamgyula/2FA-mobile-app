package me.sim05.twofactorauth

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddServicePage(navController: NavController, viewModel: ServicesViewModel = viewModel()) {
    var serviceName: String by remember { mutableStateOf("") }
    var username: String by remember { mutableStateOf("") }
    var token: String by remember { mutableStateOf("") }

    Column {
        TextField(
            value = serviceName,
            onValueChange = { serviceName = it },
            label = { Text("Service name: ") }
        )
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username: ") }
        )
        TextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Token: ") }
        )
    }
}
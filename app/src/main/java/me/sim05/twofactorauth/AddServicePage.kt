package me.sim05.twofactorauth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.sim05.twofactorauth.ui.BottomNavigationBar
import me.sim05.twofactorauth.ui.theme.TwoFactorAuthTheme
import me.sim05.twofactorauth.ui.viewModels.AppViewModelProvider
import me.sim05.twofactorauth.ui.viewModels.ServiceDetails
import me.sim05.twofactorauth.ui.viewModels.ServiceEntryViewModel
import me.sim05.twofactorauth.ui.viewModels.ServiceUiState

@Composable
fun AddServicePage(
    navController: NavController,
    viewModel: ServiceEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(bottomBar = {
        BottomNavigationBar(navController)
    }) { contentPadding ->
        ServiceEntry(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
                .padding(10.dp),
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveService()
                    navController.popBackStack()
                }
            },
            viewModel.serviceUiState
        )
    }
}

@Composable
fun ServiceEntry(
    modifier: Modifier = Modifier,
    onValueChange: (ServiceDetails) -> Unit = {},
    onSaveClick: () -> Unit,
    serviceUiState: ServiceUiState
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ServiceForm(
            onValueChange = onValueChange,
            serviceDetails = serviceUiState.serviceDetails,
        )
        Button(
            onClick = { onSaveClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Text(stringResource(R.string.create))
        }
    }
}

@Composable
fun ServiceForm(
    modifier: Modifier = Modifier,
    onValueChange: (ServiceDetails) -> Unit = {},
    serviceDetails: ServiceDetails
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CardTextField(
            modifier = Modifier.fillMaxWidth(),
            value = serviceDetails.name,
            onValueChange = { onValueChange(serviceDetails.copy(name = it)) },
            label = "Service name: ",
            leadingIcon = Icons.Filled.Star,
        )
        CardTextField(
            modifier = Modifier.fillMaxWidth(),
            value = serviceDetails.username,
            onValueChange = { onValueChange(serviceDetails.copy(username = it)) },
            label = "Username: ",
            leadingIcon = Icons.Filled.Person,
        )
        CardTextField(
            modifier = Modifier.fillMaxWidth(),
            value = serviceDetails.token,
            onValueChange = { onValueChange(serviceDetails.copy(token = it)) },
            label = "Token: ",
            leadingIcon = Icons.Filled.Lock,
        )
    }
}

@Composable
fun CardTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector
) {
    Card(
        shape = MaterialTheme.shapes.medium, modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            )
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddServicePagePreview() {
    TwoFactorAuthTheme {
        Scaffold(bottomBar = {
            BottomNavigationBar(rememberNavController())
        }) { contentPadding ->
            ServiceEntry(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxWidth()
                    .padding(10.dp),
                onValueChange = {},
                onSaveClick = {

                },
                ServiceUiState()
            )
        }
    }
}
package me.sim05.twofactorauth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun EditServicePage(
    navController: NavController,
    serviceDetails: ServiceDetails?,
    viewModel: ServiceEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    if (serviceDetails == null) {
        navController.popBackStack()
        return
    }

    val coroutineScope = rememberCoroutineScope()

    val serviceUiState = viewModel.serviceUiState
    viewModel.updateUiState(serviceDetails)

    var openDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(bottomBar = {
        BottomNavigationBar(navController)
    }
    ) { innerPadding ->
        ServiceEditEntry(
            modifier = Modifier.padding(innerPadding),
            serviceUiState = serviceUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveService()
                    navController.popBackStack()
                }
            },
            onValueChange = viewModel::updateUiState,
            onDeleteClick = {
                coroutineScope.launch {
                    viewModel.deleteService()
                    navController.popBackStack()
                }
            }
        )
        when {
            openDeleteDialog -> {
                DeleteDialog(
                    onConfirm = {
                        openDeleteDialog = false
                        coroutineScope.launch {
                            viewModel.deleteService()
                            navController.popBackStack()
                        }
                    },
                    onDismiss = { openDeleteDialog = false }
                )
            }
        }

    }
}

@Composable
fun ServiceEditEntry(
    modifier: Modifier = Modifier,
    onValueChange: (ServiceDetails) -> Unit = {},
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    serviceUiState: ServiceUiState
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ServiceForm(
            serviceDetails = serviceUiState.serviceDetails,
            onValueChange = onValueChange,
        )
        Button(
            onClick = { onSaveClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Text(stringResource(R.string.save))
        }
        Button(
            onClick = { onDeleteClick() }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            )
        ) {
            Text(stringResource(R.string.delete))
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun EditServicePagePreview() {
    TwoFactorAuthTheme {
        Scaffold(bottomBar = {
            BottomNavigationBar(rememberNavController())
        }) { contentPadding ->
            ServiceEditEntry(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxWidth()
                    .padding(10.dp),
                onValueChange = {},
                onSaveClick = {},
                onDeleteClick = {},
                ServiceUiState(
                    serviceDetails = ServiceDetails(
                        name = "Not service name",
                        username = "IsUsername",
                        token = "NotAToken"
                    )
                )
            )
            when {
                true -> {
                    DeleteDialog(
                        onConfirm = {},
                        onDismiss = {}
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(modifier = modifier, shape = MaterialTheme.shapes.medium) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.delete_service_confirmation),
                modifier = Modifier.padding(16.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { onConfirm() }) {
                    Text(stringResource(R.string.delete))
                }
                Button(onClick = { onDismiss() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }

    }
}

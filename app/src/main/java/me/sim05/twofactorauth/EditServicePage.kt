package me.sim05.twofactorauth

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.sim05.twofactorauth.ui.components.BottomNavigationBar
import me.sim05.twofactorauth.ui.theme.TwoFactorAuthTheme
import me.sim05.twofactorauth.ui.viewModels.AppViewModelProvider
import me.sim05.twofactorauth.ui.viewModels.ServiceDetails
import me.sim05.twofactorauth.ui.viewModels.ServiceEntryViewModel
import me.sim05.twofactorauth.ui.viewModels.ServiceUiState

@Composable
fun EditServicePage(
    navController: NavController,
    viewModel: ServiceEntryViewModel = viewModel(
        LocalContext.current as ComponentActivity,
        factory = AppViewModelProvider.Factory
    )
) {
    val coroutineScope = rememberCoroutineScope()
    val openDeleteDialog = remember { mutableStateOf(false) }

    Scaffold(bottomBar = {
        BottomNavigationBar(navController)
    }
    ) { innerPadding ->
        ServiceEditEntry(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(10.dp),
            serviceUiState = viewModel.serviceUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveService()
                    navController.popBackStack()
                }
            },
            onValueChange = viewModel::updateUiState,
            onDeleteClick = {
                openDeleteDialog.value = true
            }
        )
        when {
            openDeleteDialog.value -> {
                DeleteDialog(
                    onConfirm = {
                        openDeleteDialog.value = false
                        coroutineScope.launch {
                            viewModel.deleteService()
                            navController.popBackStack()
                        }
                    },
                    shouldShowDialog = openDeleteDialog
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
    val shouldShowDialog = remember { mutableStateOf(false) }

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
                        shouldShowDialog = shouldShowDialog
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteDialog(
    onConfirm: () -> Unit,
    shouldShowDialog: MutableState<Boolean>
) {
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },
            title = { Text(text = "Confirm delete") },
            text = { Text(text = "Are you sure you want to delete this session?") },
            confirmButton = { // 6
                Button(
                    onClick = {
                        onConfirm()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        shouldShowDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    )
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                    )
                }
            }
        )
    }
}

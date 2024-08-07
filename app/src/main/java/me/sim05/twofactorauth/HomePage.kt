package me.sim05.twofactorauth

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import me.sim05.twofactorauth.data.Service
import me.sim05.twofactorauth.ui.viewModels.AppViewModelProvider
import me.sim05.twofactorauth.ui.components.BottomNavigationBar
import me.sim05.twofactorauth.ui.viewModels.HomeViewModel
import me.sim05.twofactorauth.ui.theme.TwoFactorAuthTheme
import me.sim05.twofactorauth.ui.viewModels.ServiceDetails
import me.sim05.twofactorauth.ui.viewModels.ServiceEntryViewModel
import me.sim05.twofactorauth.ui.viewModels.toServiceDetails

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val serviceEntryViewModel: ServiceEntryViewModel = viewModel(LocalContext.current as ComponentActivity, factory = AppViewModelProvider.Factory)

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                Icon(Icons.Filled.Add, stringResource(R.string.add_new_2fa_service))
            }
        },
        content = { innerPadding ->
            TwoFactorAuthServices(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 20.dp),
                serviceList = homeUiState.serviceList,
                navToSetting = { serviceDetails ->
                    serviceEntryViewModel.updateUiState(serviceDetails)
                    navController.navigate(Pages.Edit.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    )
}

@Composable
fun TwoFactorAuthServices(modifier: Modifier = Modifier, serviceList: List<Service>, navToSetting: (ServiceDetails) -> Unit = {}) {
    var targetTime by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis() + 30000)
    }

    var remainingTime by remember {
        mutableLongStateOf(targetTime - System.currentTimeMillis())
    }

    var isRunning by remember { mutableStateOf(false) }
    LifecycleResumeEffect(Unit) {
        isRunning = true
        onPauseOrDispose { isRunning = false }
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            if (remainingTime <= 0) {
                targetTime = System.currentTimeMillis() + 30000
            }

            remainingTime = targetTime - System.currentTimeMillis()
            delay(1000)
        }
    }

    if (serviceList.isEmpty()) {
        Text(stringResource(R.string.no_services_added), modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(serviceList) { service ->
                TwoFactorAuthService(service = service, navToSetting = {
                    navToSetting(service.toServiceDetails())
                }, timeRemaining = remainingTime)
            }
        }
    }
}

@Composable
fun TwoFactorAuthService(modifier: Modifier = Modifier, service: Service, navToSetting: () -> Unit = {}, timeRemaining: Long) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.surfaceDim,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface,
        ),
        onClick = {
            navToSetting()
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 4.dp)
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
                    service.name,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    service.username,
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
            Countdown(
                modifier = Modifier.padding(start = 10.dp),
                timeLeft = (timeRemaining / 1000).toInt()
            )
        }
    }
}

@Composable
fun Countdown(modifier: Modifier = Modifier, timeLeft: Int) {
    Text(
        text = timeLeft.toString(),
        modifier = modifier
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun HomePagePreview() {
    TwoFactorAuthTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                    },
                ) {
                    Icon(Icons.Filled.Add, stringResource(R.string.add_new_2fa_service))
                }
            },
            content = { innerPadding ->
                TwoFactorAuthServices(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(top = 20.dp),
                    serviceList = listOf(
                        Service(0, "Service name", "Username", "000 000"),
                        Service(1, "Twitter", "Sim05", "111 111")
                    )
                )
            },
            bottomBar = {
                BottomNavigationBar(rememberNavController())
            }
        )
    }
}
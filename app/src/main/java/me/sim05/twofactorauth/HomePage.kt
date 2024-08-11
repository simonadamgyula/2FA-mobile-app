package me.sim05.twofactorauth

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import me.sim05.twofactorauth.data.Service
import me.sim05.twofactorauth.ui.components.BottomNavigationBar
import me.sim05.twofactorauth.ui.theme.TwoFactorAuthTheme
import me.sim05.twofactorauth.ui.viewModels.AppViewModelProvider
import me.sim05.twofactorauth.ui.viewModels.HomeViewModel
import me.sim05.twofactorauth.ui.viewModels.ServiceDetails
import me.sim05.twofactorauth.ui.viewModels.ServiceEntryViewModel
import me.sim05.twofactorauth.ui.viewModels.toServiceDetails
import me.sim05.twofactorauth.utils.TimerState
import me.sim05.twofactorauth.utils.formattedTotp

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val serviceEntryViewModel: ServiceEntryViewModel =
        viewModel(LocalContext.current as ComponentActivity, factory = AppViewModelProvider.Factory)
    val timerState by viewModel.timerState.observeAsState(TimerState())

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
                },
                timerState = timerState
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    )
}

@Composable
fun TwoFactorAuthServices(
    modifier: Modifier = Modifier,
    serviceList: List<Service>,
    navToSetting: (ServiceDetails) -> Unit = {},
    timerState: TimerState?
) {
    val context = LocalContext.current
    val sharedPreferences = remember { PreferenceManager(context) }

    if (serviceList.isEmpty()) {
        Column(
            modifier = modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.no_services_added), modifier = modifier)
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Log.v("test", "this runs")
            items(serviceList) { service ->
                TwoFactorAuthService(
                    service = service,
                    navToSetting = {
                        navToSetting(service.toServiceDetails())
                    },
                    timeRemaining = timerState?.timeInMillis ?: 30,
                    hidden = sharedPreferences.getBoolean("hiddenTokens", false),
                    context = context,
                    compact = sharedPreferences.getData("listStyle", "default") == "compact",
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TwoFactorAuthService(
    modifier: Modifier = Modifier,
    service: Service,
    navToSetting: () -> Unit = {},
    timeRemaining: Long,
    hidden: Boolean,
    compact: Boolean,
    context: Context
) {
    val totp = formattedTotp(service.secret.toByteArray())
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var hiding by remember { mutableStateOf(hidden) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (hiding) {
                        hiding = false
                    } else {
                        clipboardManager.setText(AnnotatedString(totp.replace(" ", "")))
                        val toast =
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                },
                onLongClick = {
                    navToSetting()
                },
            ),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.surfaceDim,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = if (compact) 4.dp else 12.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape),
                model = "https://img.logo.dev/${service.name.lowercase()}.com?token=pk_bW8CHcABQJajwBEhMWPBcg",
                contentScale = ContentScale.Crop,
                contentDescription = "Service logo",
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(if (compact) 2.dp else 4.dp)
            ) {
                Text(
                    service.name,
                    style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleLarge
                )
                Text(
                    service.username,
                    style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    if (hiding) "Click to reveal" else totp,
                    style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Countdown(
                modifier = Modifier
                    .padding(start = 10.dp, end = 8.dp)
                    .size(36.dp),
                timeLeft = (if (timeRemaining == 0L) 30 else timeRemaining).toInt()
            )
        }
    }
}

@Composable
fun Countdown(modifier: Modifier = Modifier, timeLeft: Int) {
    val color = if (timeLeft > 5) Color.White else Color.Red

    Box(modifier.drawBehind {
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = (360f * (timeLeft / 30f)) % 360f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 3.dp.toPx()
            )
        )
    }, contentAlignment = Alignment.Center) {
        Text(
            text = (timeLeft).toString(),
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
    }
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
                    ),
                    timerState = TimerState(10, 0.5f, isPlaying = true, isDone = false)
                )
            },
            bottomBar = {
                BottomNavigationBar(rememberNavController())
            }
        )
    }
}
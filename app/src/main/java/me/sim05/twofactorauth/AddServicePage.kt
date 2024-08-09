package me.sim05.twofactorauth

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.launch
import me.sim05.twofactorauth.ui.components.BottomNavigationBar
import me.sim05.twofactorauth.ui.components.CardTextField
import me.sim05.twofactorauth.ui.theme.TwoFactorAuthTheme
import me.sim05.twofactorauth.ui.viewModels.AppViewModelProvider
import me.sim05.twofactorauth.ui.viewModels.ServiceDetails
import me.sim05.twofactorauth.ui.viewModels.ServiceEntryViewModel
import me.sim05.twofactorauth.ui.viewModels.ServiceUiState
import me.sim05.twofactorauth.utils.AuthQrCodeProcessor
import me.sim05.twofactorauth.utils.TimerState
import me.sim05.twofactorauth.utils.formattedTotp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServicePage(
    navController: NavController,
    viewModel: ServiceEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val timerState by viewModel.timerState.observeAsState(TimerState())

    Scaffold(bottomBar = {
        BottomNavigationBar(navController)
    },
        topBar = {
            TopAppBar(
                title = {},
                windowInsets = WindowInsets(top = 10.dp),
                navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { contentPadding ->
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
            viewModel.serviceUiState,
            timerState = timerState
        )
    }
}

@Composable
fun ServiceEntry(
    modifier: Modifier = Modifier,
    onValueChange: (ServiceDetails) -> Unit = {},
    onSaveClick: () -> Unit,
    serviceUiState: ServiceUiState,
    timerState: TimerState
) {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
        )
        .enableAutoZoom()
        .build()

    val scanner = GmsBarcodeScanning.getClient(LocalContext.current, options)
    val timeRemaining = timerState.timeInMillis

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
        Button(onClick = {
            scanner.startScan().addOnSuccessListener { barcode ->
                val qrCodeProcessor = barcode.rawValue?.let { AuthQrCodeProcessor(it) }
                if (qrCodeProcessor == null) {
                    // TODO: Add error handling
                    Log.v("barcode", "Failed to process barcode")
                    return@addOnSuccessListener
                }

                onValueChange(
                    serviceUiState.serviceDetails.copy(
                        name = qrCodeProcessor.getIssuer(),
                        secret = qrCodeProcessor.getSecret(),
                        username = qrCodeProcessor.getDetails()
                    )
                )
            }
        }
        ) {
            Text(stringResource(R.string.scan_qr_code))
        }
        Spacer(Modifier.weight(1f))
        if (serviceUiState.serviceDetails.secret.isNotEmpty()) {
            TotpPreview(
                modifier = Modifier.padding(10.dp),
                timeRemaining = (if (timeRemaining == 0L) 30 else timeRemaining).toInt(),
                service = serviceUiState.serviceDetails
            )
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
            value = serviceDetails.secret,
            onValueChange = { onValueChange(serviceDetails.copy(secret = it)) },
            label = "Secret: ",
            leadingIcon = Icons.Filled.Lock,
        )
    }
}

@Composable
fun TotpPreview(modifier: Modifier = Modifier, timeRemaining: Int, service: ServiceDetails) {
    val totp = formattedTotp(service.secret.toByteArray())
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Card(
        border = BorderStroke(width = 1.dp, color = Color.White),
        colors = CardColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContentColor = Color.White,
        ),
        onClick = {
            clipboardManager.setText(AnnotatedString(totp.replace(" ", "")))
        }
    ) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = totp,
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Countdown(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(36.dp),
                timeLeft = timeRemaining
            )
        }
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
                ServiceUiState(),
                timerState = TimerState()
            )
        }
    }
}
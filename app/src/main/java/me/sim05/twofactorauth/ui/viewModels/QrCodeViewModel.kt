package me.sim05.twofactorauth.ui.viewModels

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class QrCodeViewModel() : ViewModel() {
    private val _uiState: MutableStateFlow<QrScanUIState> = MutableStateFlow(QrScanUIState())
    val uiState: StateFlow<QrScanUIState> = _uiState

    fun onQrCodeDetected(result: String) {
        Log.d("QR Scanner", result)
        _uiState.update { it.copy(detectedQR = result) }
    }

    fun onTargetPositioned(rect: Rect) {
        _uiState.update { it.copy(targetRect = rect) }
    }
}

data class QrScanUIState(
    val loading: Boolean = false,
    val detectedQR: String = "",
    val targetRect: Rect = Rect.Zero,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
)
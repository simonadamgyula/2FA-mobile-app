package me.sim05.twofactorauth.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.sim05.twofactorauth.data.Service
import me.sim05.twofactorauth.data.ServicesRepository

class HomeViewModel(servicesRepository: ServicesRepository) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        servicesRepository.getAllServicesStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(val serviceList: List<Service> = listOf())
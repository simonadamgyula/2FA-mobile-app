package me.sim05.twofactorauth.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import me.sim05.twofactorauth.data.Service
import me.sim05.twofactorauth.data.ServicesRepository

class ServiceEntryViewModel(private val servicesRepository: ServicesRepository) : ViewModel() {
    var serviceUiState by mutableStateOf(ServiceUiState())

    fun updateUiState(serviceDetails: ServiceDetails) {
        serviceUiState =
            ServiceUiState(serviceDetails = serviceDetails, isEntryValid = validateInput(serviceDetails))
    }

    suspend fun saveService() {
        if (validateInput()) {
            servicesRepository.insertService(serviceUiState.serviceDetails.toService())
        }
    }

    private fun validateInput(uiState: ServiceDetails = serviceUiState.serviceDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && username.isNotBlank() && token.isNotBlank()
        }
    }
}

data class ServiceUiState(
    val serviceDetails: ServiceDetails = ServiceDetails(),
    val isEntryValid: Boolean = false
)

data class ServiceDetails(
    val id: Int = 0,
    val name: String = "",
    val username: String = "",
    val token: String = "",
)

fun ServiceDetails.toService(): Service = Service(
    id = id,
    name = name,
    username = username,
    token = token
)

fun Service.toServiceUiState(isEntryValid: Boolean = false): ServiceUiState = ServiceUiState(
    serviceDetails = this.toServiceDetails(),
    isEntryValid = isEntryValid
)

fun Service.toServiceDetails(): ServiceDetails = ServiceDetails(
    id = id,
    name = name,
    username = username,
    token = token
)
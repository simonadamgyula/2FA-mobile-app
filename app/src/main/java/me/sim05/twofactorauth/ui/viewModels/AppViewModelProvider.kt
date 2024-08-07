package me.sim05.twofactorauth.ui.viewModels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import me.sim05.twofactorauth.ServicesApplication

class AppViewModelProvider {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ServiceEntryViewModel(servicesApplication().container.servicesRepository)
            }
            initializer {
                HomeViewModel(servicesApplication().container.servicesRepository)
            }
        }
    }
}

fun CreationExtras.servicesApplication(): ServicesApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ServicesApplication)
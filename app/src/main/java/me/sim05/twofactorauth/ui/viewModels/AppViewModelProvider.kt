package me.sim05.twofactorauth.ui.viewModels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import me.sim05.twofactorauth.ServicesApplication
import me.sim05.twofactorauth.utils.RepeatingTimer
import kotlin.time.Duration.Companion.seconds

class AppViewModelProvider {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            val repeatingTimer = RepeatingTimer(30.seconds.inWholeMilliseconds)

            initializer {
                ServiceEntryViewModel(servicesApplication().container.servicesRepository, repeatingTimer = repeatingTimer)
            }
            initializer {
                repeatingTimer.start()
                HomeViewModel(servicesApplication().container.servicesRepository, repeatingTimer)
            }
        }
    }
}

fun CreationExtras.servicesApplication(): ServicesApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ServicesApplication)
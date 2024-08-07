package me.sim05.twofactorauth

import android.app.Application
import me.sim05.twofactorauth.data.AppContainer
import me.sim05.twofactorauth.data.AppDataContainer

class ServicesApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
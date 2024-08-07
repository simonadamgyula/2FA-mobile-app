package me.sim05.twofactorauth.data
import android.content.Context

interface AppContainer {
    val servicesRepository: ServicesRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    override val servicesRepository: ServicesRepository by lazy {
        OfflineServicesRepository(ServicesDatabase.getDatabase(context).serviceDao())
    }
}
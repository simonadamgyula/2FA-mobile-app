package me.sim05.twofactorauth.data

import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    fun getAllServicesStream(): Flow<List<Service>>

    fun getServiceStream(id: Int): Flow<Service?>

    suspend fun insertService(service: Service)

    suspend fun deleteService(service: Service)

    suspend fun updateService(service: Service)
}
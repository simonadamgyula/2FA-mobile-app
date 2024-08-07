package me.sim05.twofactorauth.data

import kotlinx.coroutines.flow.Flow

class OfflineServicesRepository(private val serviceDao: ServiceDao): ServicesRepository {
    override fun getAllServicesStream(): Flow<List<Service>> = serviceDao.getServices()

    override fun getServiceStream(id: Int): Flow<Service?> = serviceDao.getService(id)

    override suspend fun insertService(service: Service) = serviceDao.insert(service)

    override suspend fun deleteService(service: Service) = serviceDao.delete(service)

    override suspend fun updateService(service: Service) = serviceDao.update(service)
}
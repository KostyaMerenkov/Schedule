package ru.dvfu.appliances.model.repository_offline

import kotlinx.coroutines.flow.Flow
import ru.dvfu.appliances.model.repository.entity.Appliance

interface OfflineRepository {
    suspend fun getAppliances(): Flow<List<Appliance>>
    suspend fun getApplianceById()
}
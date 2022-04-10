package ru.dvfu.appliances.model.repository_offline

import kotlinx.coroutines.flow.Flow
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User

interface OfflineRepository {
    suspend fun getUser(userId: String): Flow<Result<User>>

    fun getAppliances(): Flow<Result<List<Appliance>>>
    fun getApplianceById(applianceId: String): Flow<Result<Appliance>>
}
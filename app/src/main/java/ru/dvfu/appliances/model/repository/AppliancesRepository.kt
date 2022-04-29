package ru.dvfu.appliances.model.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.Progress

interface AppliancesRepository {
    suspend fun addUsersToAppliance(appliance: Appliance, userIds: List<String>): Result<Unit>
    suspend fun addSuperUsersToAppliance(appliance: Appliance, superuserIds: List<String>): Result<Unit>

    suspend fun addAppliance(appliance: Appliance): Result<Unit>
    suspend fun getAppliances(): Flow<List<Appliance>>
    suspend fun getAppliancesOneTime(): Result<List<Appliance>>
    suspend fun getApplianceUsers(userIds: List<String>): Flow<List<User>>
    suspend fun getAppliance(applianceId: String): Flow<Result<Appliance>>

    suspend fun deleteAppliance(appliance: Appliance): Result<Unit>
    suspend fun deleteUserFromAppliance(userIdToDelete: String, from: Appliance): Result<Unit>
    suspend fun deleteSuperUserFromAppliance(userIdToDelete: String, from: Appliance): Result<Unit>
    suspend fun getSuperUserAppliances(userId: String): Flow<List<Appliance>>
    suspend fun getUserAppliances(userId: String): Flow<List<Appliance>>
}
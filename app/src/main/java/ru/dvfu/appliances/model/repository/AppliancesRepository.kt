package ru.dvfu.appliances.model.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.Progress

interface AppliancesRepository {
    suspend fun addUsersToAppliance(appliance: Appliance, userIds: List<String>): StateFlow<Progress>
    suspend fun addSuperUsersToAppliance(appliance: Appliance, superuserIds: List<String>): StateFlow<Progress>

    suspend fun addAppliance(appliance: Appliance): StateFlow<Progress>
    suspend fun getAppliances(): Flow<List<Appliance>>
    suspend fun getApplianceUsers(userIds: List<String>): Flow<List<User>>
    suspend fun getAppliance(applianceId: String): Flow<Result<Appliance>>

    suspend fun deleteAppliance(appliance: Appliance)
    suspend fun deleteUserFromAppliance(userToDelete: User, from: Appliance)
    suspend fun deleteSuperUserFromAppliance(userToDelete: User, from: Appliance)
    suspend fun getSuperUserAppliances(userId: String): Flow<List<Appliance>>
    suspend fun getUserAppliances(userId: String): Flow<List<Appliance>>
}
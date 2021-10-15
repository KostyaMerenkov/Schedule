package ru.dvfu.appliances.model.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.userdata.entities.Appliance
import ru.dvfu.appliances.ui.Progress

interface DatabaseProvider {
    suspend fun addNewUser(user: User): StateFlow<Progress>
    suspend fun getUsers(): Flow<List<User>>
    suspend fun getAppliances(): Flow<List<Appliance>>

    suspend fun addUser(user: User)
    suspend fun addAppliance(appliance: Appliance)
    /*suspend fun addNewCatch(markerId: String, newCatch: RawUserCatch): StateFlow<Progress>
    suspend fun addNewMarker(newMarker: RawMapMarker): StateFlow<Progress>
    suspend fun deleteMarker(userMapMarker: UserMapMarker)
    suspend fun deleteCatch(userCatch: UserCatch)
    fun getMapMarker(markerId: String): Flow<UserMapMarker?>
    fun getAllMarkers(): Flow<MapMarker>
    fun getAllUserMarkersList(): Flow<List<MapMarker>>
    fun getAllUserCatchesList(): Flow<List<UserCatch>>
    fun getAllUserCatchesState(): Flow<CatchesContentState>
    fun getCatchesByMarkerId(markerId: String): Flow<List<UserCatch>>*/
}
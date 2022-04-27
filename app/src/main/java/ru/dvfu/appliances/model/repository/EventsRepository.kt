package ru.dvfu.appliances.model.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.dvfu.appliances.model.repository.entity.*
import ru.dvfu.appliances.ui.Progress
import java.time.LocalDate

interface EventsRepository {
    suspend fun addNewEvent(event: Event): Result<Unit>

    suspend fun getAllEvents(): Flow<List<Event>>
    suspend fun getAllEventsFromDate(date: LocalDate): Flow<List<Event>>
    suspend fun deleteEvent(eventToDelete: CalendarEvent): Result<Unit>
    suspend fun setNewTimeEnd(eventId: String, timeEnd: Long): Result<Unit>
    suspend fun setNewEventStatus(eventId: String, newStatus: BookingStatus): Result<Unit>
    suspend fun updateEvent(eventId: String, data: Map<String, Any?>): Result<Unit>

    suspend fun getAllEventsForOneDay(date: LocalDate): Flow<List<Event>>

    suspend fun getApplianceEventsAfterTime(applianceId: String, time: Long): Result<List<Event>>
    suspend fun getApplianceDateEvents(applianceId: String, date: LocalDate): Result<List<Event>>
    suspend fun getAllEventsWithPeriod(dateStart: LocalDate, dateEnd: LocalDate): Result<List<Event>>
    /*suspend fun getUsers(): Flow<List<User>>

    suspend fun addUsersToAppliance(appliance: Appliance, userIds: List<String>): StateFlow<Progress>
    suspend fun addSuperUsersToAppliance(appliance: Appliance, superuserIds: List<String>): StateFlow<Progress>



    suspend fun addAppliance(appliance: Appliance): StateFlow<Progress>
    suspend fun getAppliances(): Flow<List<Appliance>>
    suspend fun getApplianceUsers(userIds: List<String>): Flow<List<User>>
    suspend fun getAppliance(appliance: Appliance): Flow<Appliance>

    suspend fun deleteAppliance(appliance: Appliance)
    suspend fun deleteUserFromAppliance(userToDelete: User, from: Appliance)
    suspend fun deleteSuperUserFromAppliance(userToDelete: User, from: Appliance)
    suspend fun getSuperUserAppliances(userId: String): Flow<List<Appliance>>
    suspend fun getUserAppliances(userId: String): Flow<List<Appliance>>*/


}
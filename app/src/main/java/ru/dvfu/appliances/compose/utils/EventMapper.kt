package ru.dvfu.appliances.compose.utils

import kotlinx.coroutines.flow.first
import ru.dvfu.appliances.compose.use_cases.*
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.toLocalDate
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.model.utils.toMillis

class EventMapper(
    private val getUserUseCase: GetUserUseCase,
    private val getApplianceUseCase: GetApplianceUseCase,
) {

    /**
     * fun mapEvents @returns listOf CalendarEvent
     */
    suspend fun mapEvents(list: List<Event>) = list.map { mapEventToCalendarEvent(it) }

    suspend fun mapEvent(event: Event) = listOf(event).map { mapEventToCalendarEvent(it) }.first()

    suspend fun mapEvent(event: CalendarEvent) = listOf(event).map { mapCalendarEventToEvent(it) }.first()

    private fun mapCalendarEventToEvent(calendarEvent: CalendarEvent) = with(calendarEvent) {
        Event(
            id = id,
            date = this.date.toMillis,
            timeCreated = timeCreated.toMillis,
            timeStart = timeStart.toMillis,
            timeEnd = timeEnd.toMillis,
            commentary = commentary,
            userId = user?.userId ?: "",
            applianceId = appliance?.id ?: "",
            managedById = managedUser?.userId,
            managedTime = managedTime?.toMillis,
            managerCommentary = managerCommentary,
            status = status,
        )
    }


    private suspend fun mapEventToCalendarEvent(currentEvent: Event) = with(currentEvent) {
        CalendarEvent(
            id = id,
            date = this.date.toLocalDate(),
            timeCreated = timeCreated.toLocalDateTime(),
            timeStart = currentEvent.timeStart.toLocalDateTime(),
            timeEnd = currentEvent.timeEnd.toLocalDateTime(),
            commentary = currentEvent.commentary,
            user = getUserUseCase(userId).first().getOrDefault(User()),
            appliance = getApplianceUseCase(applianceId).first().getOrDefault(Appliance()),
            managedUser = managedById?.let { getUserUseCase(managedById).first().getOrDefault(User()) },
            managedTime = managedTime?.toLocalDateTime(),
            managerCommentary = managerCommentary,
            status = status,
        )
    }
}

package ru.dvfu.appliances.compose.use_cases

import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent

class UpdateEventUseCase(val repository: EventsRepository) {
    suspend operator fun invoke(eventId: String, event: CalendarEvent): Result<Unit> {
        return repository.updateEvent(
            eventId = eventId,
            data = mapOf<String, Any?>(
                "id" to event.id,
                "date" to event.date.toMillis,
                "timeCreated" to event.timeCreated.toMillis,
                "timeStart" to event.timeStart.toMillis,
                "timeEnd" to event.timeEnd.toMillis,
                "commentary" to event.commentary,
                "applianceId" to (event.appliance?.id ?: "0"),
                "userId" to (event.user?.userId ?: "0"),
                "managedById" to (event.managedUser?.userId ?: "0"),
                "managedTime" to event.managedTime?.toMillis,
                "managerCommentary" to event.managerCommentary,
                "status" to event.status
            )
        )
    }
}
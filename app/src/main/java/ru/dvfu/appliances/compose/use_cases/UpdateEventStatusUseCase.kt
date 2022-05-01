package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.compose.utils.NotificationManager
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent

class UpdateEventStatusUseCase(
    private val eventsRepository: EventsRepository,
    private val notificationManager: NotificationManager,
) {
    suspend operator fun invoke(event: CalendarEvent, newStatus: BookingStatus) = flow<Result<Unit>>{
        val result = eventsRepository.setNewEventStatus(event.id, newStatus)
        if (result.isSuccess) {
            notificationManager.newEventStatus(event, newStatus)
        }
        emit(result)
    }
}
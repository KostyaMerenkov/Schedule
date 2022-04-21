package ru.dvfu.appliances.compose.use_cases

import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent

class UpdateEventStatusUseCase(
    private val eventsRepository: EventsRepository,
) {
    suspend operator fun invoke(eventId: String, newStatus: BookingStatus) {
        eventsRepository.setNewEventStatus(eventId, newStatus)
    }
}
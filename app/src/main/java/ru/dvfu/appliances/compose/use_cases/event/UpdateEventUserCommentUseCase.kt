package ru.dvfu.appliances.compose.use_cases.event

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.compose.utils.NotificationManager
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent

class UpdateEventUserCommentUseCase(
    private val eventsRepository: EventsRepository,
) {
    suspend operator fun invoke(event: CalendarEvent, newComment: String) = flow<Result<Unit>>{
        val result = eventsRepository.updateEvent(event.id, mapOf("commentary" to newComment))
        emit(result)
    }
}
package ru.dvfu.appliances.compose.use_cases.event

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.CalendarEvent

class UpdateManagerCommentUseCase(
    private val eventsRepository: EventsRepository,
) {
    suspend operator fun invoke(event: CalendarEvent, newComment: String) = flow<Result<Unit>> {
        val result =
            eventsRepository.updateEvent(event.id, mapOf("managerCommentary" to newComment))
        emit(result)
    }
}
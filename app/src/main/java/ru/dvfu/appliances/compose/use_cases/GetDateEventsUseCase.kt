package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Event
import java.time.LocalDate

class GetDateEventsUseCase(
    private val eventsRepository: EventsRepository,
) {

    suspend operator fun invoke(
        date: LocalDate
    ) = flow<List<Event>> {
        eventsRepository.getAllEventsForDay(date).collect {
            emit(it)
        }
    }

}

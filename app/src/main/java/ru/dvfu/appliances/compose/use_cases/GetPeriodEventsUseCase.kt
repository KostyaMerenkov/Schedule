package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Event
import java.time.LocalDate

class GetPeriodEventsUseCase(
    private val eventsRepository: EventsRepository,
) {

    suspend operator fun invoke(
        dateStart: LocalDate,
        dateEnd: LocalDate,
    ) = flow<List<Event>> {
        eventsRepository.getAllEventsWithPeriod(dateStart, dateEnd).fold(
            onSuccess = {
                emit(it)
            },
            onFailure = {
                emit(listOf())
            })
    }

}

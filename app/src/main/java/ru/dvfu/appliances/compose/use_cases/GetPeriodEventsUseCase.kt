package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.utils.toLocalDate
import java.time.LocalDate

class GetPeriodEventsUseCase(
    private val eventsRepository: EventsRepository,
) {

    suspend operator fun invoke(
        dateStart: LocalDate,
        dateEnd: LocalDate,
    ) = flow<Map<LocalDate, List<Event>>> {
        eventsRepository.getAllEventsWithPeriod(dateStart, dateEnd).fold(
            onSuccess = { list ->
                emit(list.groupBy { it.date.toLocalDate() })
            },
            onFailure = {
                emit(mapOf())
            })
    }

}

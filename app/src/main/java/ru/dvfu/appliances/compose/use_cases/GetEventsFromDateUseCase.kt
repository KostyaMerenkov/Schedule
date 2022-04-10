package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.compose.utils.AvailabilityState
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.utils.toLocalDate
import java.time.LocalDate

class GetEventsFromDateUseCase(
    private val eventsRepository: EventsRepository,
) {

    suspend operator fun invoke(
        date: LocalDate
    ) = flow<Map<LocalDate, List<Event>>> {
        eventsRepository.getAllEventsFromDate(date).collect {
            emit(it.groupBy { it.timeStart.toLocalDate() } )
        }
    }

}

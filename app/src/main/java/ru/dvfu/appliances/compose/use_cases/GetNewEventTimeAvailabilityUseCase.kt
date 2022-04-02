package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.compose.utils.AvailabilityState
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Event

class GetNewEventTimeAvailabilityUseCase(
    private val eventsRepository: EventsRepository,
) {

    suspend operator fun invoke(
        applianceId: String, timeStart: Long, timeEnd: Long
    ) = flow<AvailabilityState> {
        eventsRepository.getApplianceEventsAfterTime(applianceId, timeStart).fold(
            onSuccess = {
                if (it.isEmpty()) emit(AvailabilityState.Available)
                else {
                    if (isTimeFree(list = it.filter { it.timeStart < timeEnd }, eventStart = timeStart, eventEnd = timeEnd))
                        emit(AvailabilityState.Available)
                    else { emit(AvailabilityState.NotAvailable) }
                }
            },
            onFailure = { emit(AvailabilityState.Error) }
        )
    }

    private fun isTimeFree(list: List<Event>, eventStart: Long, eventEnd: Long): Boolean {
        list.forEach {
            if (it.timeEnd in (eventStart + 1) until eventEnd ||
                it.timeStart in (eventStart + 1) until eventEnd
            ) return false
        }
        return true
    }
}

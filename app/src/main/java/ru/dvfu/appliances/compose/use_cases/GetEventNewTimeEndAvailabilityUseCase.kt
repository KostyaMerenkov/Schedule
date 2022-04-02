package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.compose.utils.AvailabilityState
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Event

class GetEventNewTimeEndAvailabilityUseCase(
    private val eventsRepository: EventsRepository,
) {

    suspend operator fun invoke(
        eventId: String, applianceId: String, timeEnd: Long, newTimeEnd: Long
    ) = flow<AvailabilityState> {
        eventsRepository.getApplianceEventsAfterTime(applianceId, timeEnd).fold(
            onSuccess = {
                if (it.isEmpty()) emit(AvailabilityState.Available)
                else {
                    if (isTimeFree(list = it.filter { it.id != eventId }, newTimeEnd = newTimeEnd))
                        emit(AvailabilityState.Available)
                    else {
                        emit(AvailabilityState.NotAvailable)
                        //SnackbarManager.showMessage(R.string.time_not_free)
                    }
                }
            },
            onFailure = {
                emit(AvailabilityState.Error)
                //SnackbarManager.showMessage(R.string.new_event_time_end_failed)
            }
        )
    }

    private fun isTimeFree(list: List<Event>, newTimeEnd: Long): Boolean {
        list.forEach { if (it.timeStart < newTimeEnd) return false }
        return true
    }
}

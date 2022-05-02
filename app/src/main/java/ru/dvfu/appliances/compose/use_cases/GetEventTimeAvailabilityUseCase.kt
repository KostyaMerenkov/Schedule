package ru.dvfu.appliances.compose.use_cases

import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.compose.utils.AvailabilityState
import ru.dvfu.appliances.compose.viewmodels.EventDateAndTime
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.utils.isNetworkAvailable
import ru.dvfu.appliances.model.utils.toLocalTime
import ru.dvfu.appliances.model.utils.toMillis

class GetEventTimeAvailabilityUseCase(
    private val eventsRepository: EventsRepository,
) {

    suspend operator fun invoke(
        applianceId: String,
        eventDateAndTime: EventDateAndTime,
        event: CalendarEvent? = null
    ) = flow<AvailabilityState> {
        if (isNetworkAvailable(Firebase.app.applicationContext)) {
            eventsRepository.getApplianceDateEvents(applianceId, eventDateAndTime.date).fold(
                onSuccess = { result ->
                    val events = result.filter { it.id != event?.id }
                    if (events.isEmpty()) emit(AvailabilityState.Available)
                    else {
                        if (isTimeFree(list = events, eventDateAndTime))
                            emit(AvailabilityState.Available)
                        else {
                            emit(AvailabilityState.NotAvailable)
                        }
                    }
                },
                onFailure = { emit(AvailabilityState.Error) }
            )
        } else emit(AvailabilityState.Error)
    }

    private fun isTimeFree(list: List<Event>, eventDateAndTime: EventDateAndTime): Boolean {
        val timeStart = eventDateAndTime.timeStart.atDate(eventDateAndTime.date).toMillis
        val timeEnd = eventDateAndTime.timeEnd.atDate(eventDateAndTime.date).toMillis

        return !list.any {
            (timeStart > it.timeStart && timeEnd < it.timeEnd)
                    || (it.timeEnd in (timeStart + 1) until timeEnd || it.timeStart in (timeStart + 1) until timeEnd)
        }
    }
}

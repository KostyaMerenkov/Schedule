package ru.dvfu.appliances.compose.use_cases.event

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.use_cases.GetNewEventTimeAvailabilityUseCase
import ru.dvfu.appliances.compose.utils.NotificationManager
import ru.dvfu.appliances.compose.viewmodels.CalendarEventDateAndTime
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.utils.toMillis
import java.time.LocalDate
import java.time.LocalDateTime

class UpdateTimeUseCase(
    private val eventsRepository: EventsRepository,
    //private val getEventNewTimeAvailabilityUseCase: GetEventNewTimeAvailabilityUseCase,
) {
    suspend operator fun invoke(
        event: CalendarEvent,
        eventDateAndTime: CalendarEventDateAndTime,
    ) = flow<Result<Unit>> {

        // TODO: addTimeCheck availability
        val result = eventsRepository.updateEvent(
            event.id, mapOf(
                "date" to eventDateAndTime.date.toMillis,
                "timeStart" to eventDateAndTime.timeStart.atDate(eventDateAndTime.date).toMillis,
                "timeEnd" to eventDateAndTime.timeEnd.atDate(eventDateAndTime.date).toMillis
            )
        )
        emit(result)
    }

    /* val couldEditTimeEnd: MutableStateFlow<Boolean>
        get() = MutableStateFlow<Boolean>(
            couldDeleteEvent.value ||
                    Duration.between(event.value.timeEnd, LocalDateTime.now()) > MIN_EVENT_DURATION
                    && _appliance.value.superuserIds.contains(currentUser.value.userId)
        )


    val couldEditTimeStart: MutableStateFlow<Boolean>
        get() = MutableStateFlow(
            couldDeleteEvent.value ||
                    Duration.between(event.value.timeStart, LocalDateTime.now()) < MIN_EVENT_DURATION
                    && _appliance.value.superuserIds.contains(currentUser.value.userId)
        )*/


    /*fun onTimeEndChange(newTime: LocalTime) {
        viewModelScope.launch {
            _timeEndChangeState.value = UiState.InProgress
            val oldDate = event.value.timeEnd.toLocalDate()
            val oldTime = event.value.timeEnd.toLocalTime()
            val newLocalTime = newTime.atDate(oldDate)
            if (oldTime.isAfter(newTime) && currentUser.value.isAdmin()) {
                when {
                    oldDate == LocalDate.now() && newTime.isBefore(
                        LocalTime.now().plusMinutes(10)
                    ) -> {
                        _timeEndChangeState.value = UiState.Error
                        SnackbarManager.showMessage(R.string.time_end_is_before_now)
                    }
                    newTime.isBefore(event.value.timeStart.toLocalTime()) -> {
                        SnackbarManager.showMessage(R.string.time_end_is_before_start)
                    }
                    else -> saveNewTimeEnd(newLocalTime)
                }
            } else {
                getEventNewTimeEndAvailabilityUseCase(
                    eventId = eventArg.id,
                    applianceId = eventArg.appliance?.id ?: "0",
                    timeEnd = eventArg.timeEnd.toMillis,
                    newTimeEnd = newLocalTime.toMillis
                ).collect { result ->
                    when (result) {
                        AvailabilityState.Available -> saveNewTimeEnd(newLocalTime)
                        AvailabilityState.Error -> {
                            _timeEndChangeState.value = UiState.Error
                            SnackbarManager.showMessage(R.string.new_event_time_end_failed)
                        }
                        AvailabilityState.NotAvailable -> {
                            _timeEndChangeState.value = UiState.Error
                            SnackbarManager.showMessage(R.string.time_not_free)
                        }
                    }
                }
            }
        }
    }*/

    /*fun onTimeStartChange(newTime: LocalTime) {
        viewModelScope.launch {
            _timeEndChangeState.value = UiState.InProgress
            val oldDate = event.value.timeEnd.toLocalDate()
            val oldTime = event.value.timeEnd.toLocalTime()
            val newLocalDateTime = newTime.atDate(oldDate)
            if (oldTime.isBefore(newTime) && currentUser.value.isAdmin()) {
                when {
                    oldDate == LocalDate.now() && newTime.isBefore(
                        LocalTime.now().plusMinutes(10)
                    ) -> {
                        _timeEndChangeState.value = UiState.Error
                        SnackbarManager.showMessage(R.string.time_end_is_before_now)
                    }
                    newTime.isBefore(event.value.timeStart.toLocalTime()) -> {
                        SnackbarManager.showMessage(R.string.time_end_is_before_start)
                    }
                    else -> saveNewTimeEnd(newLocalDateTime)
                }
            } else {
                getEventNewTimeEndAvailabilityUseCase(
                    eventId = eventArg.id,
                    applianceId = eventArg.appliance?.id ?: "0",
                    timeEnd = eventArg.timeEnd.toMillis,
                    newTimeEnd = newLocalDateTime.toMillis
                ).collect { result ->
                    when (result) {
                        AvailabilityState.Available -> saveNewTimeEnd(newLocalDateTime)
                        AvailabilityState.Error -> {
                            _timeEndChangeState.value = UiState.Error
                            SnackbarManager.showMessage(R.string.new_event_time_end_failed)
                        }
                        AvailabilityState.NotAvailable -> {
                            _timeEndChangeState.value = UiState.Error
                            SnackbarManager.showMessage(R.string.time_not_free)
                        }
                    }
                }
            }
        }
    }

    private fun saveNewTimeEnd(newTime: LocalDateTime) {
        viewModelScope.launch {
            eventsRepository.setNewTimeEnd(
                eventId = event.value.id,
                timeEnd = newTime.toMillis
            ).fold(
                onSuccess = {
                    _timeEndChangeState.value = UiState.Success
                    _event.value = event.value.copy(timeEnd = newTime)
                },
                onFailure = {
                    _timeEndChangeState.value = UiState.Error
                    SnackbarManager.showMessage(R.string.new_event_time_end_failed)
                }
            )
        }
    }*/
}
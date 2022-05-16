package ru.dvfu.appliances.compose.use_cases.event

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import ru.dvfu.appliances.compose.components.toDate
import ru.dvfu.appliances.compose.components.toTime
import ru.dvfu.appliances.compose.use_cases.GetEventTimeAvailabilityUseCase
import ru.dvfu.appliances.compose.utils.AvailabilityState
import ru.dvfu.appliances.compose.utils.NotificationManager
import ru.dvfu.appliances.compose.viewmodels.EventDateAndTime
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.utils.toLocalDate
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.model.utils.toLocalTime
import ru.dvfu.appliances.model.utils.toMillis
import kotlin.coroutines.suspendCoroutine

class UpdateTimeUseCase(
    private val eventsRepository: EventsRepository,
    private val getEventTimeAvailabilityUseCase: GetEventTimeAvailabilityUseCase,
    private val notificationManager: NotificationManager,
) {
    suspend operator fun invoke(
        event: CalendarEvent,
        eventDateAndTime: EventDateAndTime,
    ) = flow<EventTimeUpdateResult> {

        //TODO("Check for properly working")
        val isTimeFree = checkNewEventTime(eventDateAndTime).single()

        val availabilityState = getEventTimeAvailabilityUseCase(
            applianceId = event.appliance.id,
            eventDateAndTime = eventDateAndTime,
            event = event
        ).single()
        when (availabilityState) {
            AvailabilityState.Available -> {
                eventsRepository.updateEvent(
                    event.id, mapOf(
                        "date" to eventDateAndTime.date.toMillis,
                        "timeStart" to eventDateAndTime.timeStart.atDate(eventDateAndTime.date).toMillis,
                        "timeEnd" to eventDateAndTime.timeEnd.atDate(eventDateAndTime.date).toMillis
                    )
                ).fold(
                    onSuccess = {
                        notificationManager.eventTimeChanged(event, eventDateAndTime)
                        emit(EventTimeUpdateResult.Success)
                    },
                    onFailure = {
                        emit(EventTimeUpdateResult.Error)
                    }
                )
            }
            AvailabilityState.Error -> emit(EventTimeUpdateResult.Error)
            AvailabilityState.NotAvailable -> emit(EventTimeUpdateResult.TimeNotFree)
        }

    }

    private suspend fun checkNewEventTime(eventDateAndTime: EventDateAndTime) = flow<Boolean> {
        eventsRepository.getAllEventsWithPeriod(
            dateStart = eventDateAndTime.date,
            dateEnd = eventDateAndTime.date
        ).fold(
            onSuccess = {
                if (it.isEmpty()) {
                    emit(true)
                } else {
                    val result = it.find { event ->
                        (event.timeStart.toLocalTime().isBefore(eventDateAndTime.timeStart) ||
                                event.timeStart.toLocalTime()
                                    .isAfter(eventDateAndTime.timeEnd)) && (
                                event.timeEnd.toLocalTime()
                                    .isBefore(eventDateAndTime.timeStart) || event.timeEnd.toLocalTime()
                                    .isAfter(eventDateAndTime.timeEnd))
                    }
                    emit(result == null)
                }
            },
            onFailure = {
                emit(false)
            }
        )
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

sealed class EventTimeUpdateResult {
    object Success : EventTimeUpdateResult()
    object TimeNotFree : EventTimeUpdateResult()
    object Error : EventTimeUpdateResult()
}

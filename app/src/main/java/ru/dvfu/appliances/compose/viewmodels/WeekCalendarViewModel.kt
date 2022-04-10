package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.event_calendar.CalendarEvent
import ru.dvfu.appliances.compose.use_cases.GetDateEventsUseCase
import ru.dvfu.appliances.compose.use_cases.GetEventsFromDateUseCase
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.ui.ViewState
import java.time.LocalDate

class WeekCalendarViewModel(
    private val usersRepository: UsersRepository,
    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository,
    private val userDatastore: UserDatastore,
    private val getDateEventsUseCase: GetDateEventsUseCase,
    private val getEventsFromDateUseCase: GetEventsFromDateUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User>(User())
    val currentUser = _currentUser.asStateFlow()

    private var _dayEvents =
        mutableStateMapOf<LocalDate, EventsState>(Pair(LocalDate.now(), EventsState.Loading))
    val dayEvents = _dayEvents

    /*private var _monthEvents =
        mutableStateMapOf<LocalDate, List<Event>>(Pair(LocalDate.now(), listOf()))
    val monthEvents = _monthEvents*/

    private var _monthEvents =
        MutableStateFlow(listOf<Event>())
    val monthEvents = _monthEvents
/*
    private val _dayEvents = MutableStateFlow<WeekEvents>(WeekEvents())
    val dayEvents: StateFlow<WeekEvents> = _dayEvents.asStateFlow()*/

    private val appliances = MutableStateFlow<List<Appliance>>(listOf())

    init {
        getDayEvents(LocalDate.now())
        getLatestEvents()
        getCurrentUser()
    }

    private fun getLatestEvents() {
        viewModelScope.launch {
            getEventsFromDateUseCase(LocalDate.now().minusMonths(2)).collectLatest {
                it.forEach { localDate, list ->
                    _dayEvents = _dayEvents.apply {
                        put(
                            localDate,
                            EventsState.Loaded(list.map { currentEvent ->
                                CalendarEvent(
                                    id = currentEvent.id,
                                    color = Color(currentEvent.color),
                                    applianceName = currentEvent.applianceName,
                                    applianceId = currentEvent.applianceId,
                                    userId = currentEvent.userId,
                                    superUserId = currentEvent.superUserId,
                                    start = currentEvent.timeStart.toLocalDateTime(),
                                    end = currentEvent.timeEnd.toLocalDateTime(),
                                    description = currentEvent.commentary,
                                )
                            })
                        )
                    }
                }
            }
        }
    }

    private fun getDayEvents(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            getDateEventsUseCase(date).collectLatest {
                val dayEvents = it.map { currentEvent ->
                    CalendarEvent(
                        id = currentEvent.id,
                        color = Color(currentEvent.color),
                        applianceName = currentEvent.applianceName,
                        applianceId = currentEvent.applianceId,
                        userId = currentEvent.userId,
                        superUserId = currentEvent.superUserId,
                        start = currentEvent.timeStart.toLocalDateTime(),
                        end = currentEvent.timeEnd.toLocalDateTime(),
                        description = currentEvent.commentary
                    )
                }.toList()
                _dayEvents = _dayEvents.apply {
                    put(date, EventsState.Loaded(dayEvents))
                }
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            userDatastore.getCurrentUser.collect {

            }
        }
    }

    fun onDaySelected(day: LocalDate) {
        getDayEvents(day)
    }
}

sealed class EventsState() {
    object Loading : EventsState()
    class Loaded(val events: List<CalendarEvent>) : EventsState()
}


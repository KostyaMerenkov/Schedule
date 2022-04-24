package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.utils.EventMapper
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.calendars.CalendarType
import ru.dvfu.appliances.compose.use_cases.*
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import java.time.LocalDate


class WeekCalendarViewModel(
    private val usersRepository: UsersRepository,
    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository,
    private val userDatastore: UserDatastore,
    private val getEventsFromDateUseCase: GetEventsFromDateUseCase,
    private val getPeriodEventsUseCase: GetPeriodEventsUseCase,
    private val getDateEventsUseCase: GetDateEventsUseCase,
    private val eventMapper: EventMapper,
) : ViewModel() {

    private val _calendarType = MutableStateFlow<CalendarType>(CalendarType.WEEK)
    val calendarType = _calendarType.asStateFlow()

    private val _currentDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val currentDate = _currentDate.asStateFlow()

    private val _threeDaysEvents = MutableStateFlow<List<CalendarEvent>>(listOf())
    val threeDaysEvents = _threeDaysEvents.asStateFlow()

    private val _reposEvents = MutableStateFlow<Set<Event>>(setOf())
    val selectedEvent = mutableStateOf<CalendarEvent?>(null)

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
        getCalendarTypeListener()
    }

    private fun getCalendarTypeListener() {
        viewModelScope.launch {
            userDatastore.getCalendarType.collect {
                _calendarType.value = it
            }
        }
    }

    private fun getLatestEvents() {
        viewModelScope.launch {
            getEventsFromDateUseCase(LocalDate.now().minusMonths(2)).collectLatest {
                it.forEach { (localDate, list) ->
                    _reposEvents.value = (_reposEvents.value.plus(list))
                    _dayEvents = _dayEvents.apply {
                        put(localDate, EventsState.Loaded(eventMapper.mapEvents(list)))
                    }
                }
            }
        }
    }

    private fun getDayEvents(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            _dayEvents = _dayEvents.apply {
                put(date, EventsState.Loading)
            }
            getDateEventsUseCase(date).collectLatest {
                _reposEvents.value = (_reposEvents.value.plus(it))
                val dayEvents = eventMapper.mapEvents(it)
                _dayEvents = _dayEvents.apply {
                    replace(date, EventsState.Loaded(dayEvents))?.let {
                        put(date, EventsState.Loaded(dayEvents))
                    }
                }
            }
        }

    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            userDatastore.getCurrentUser.collect {
                _currentUser.value = it
            }
        }
    }

    fun onDaySelected(day: LocalDate) {
        if (currentDate.value != day) {
            _currentDate.value = day
            getDayEvents(day)
        }
    }

    fun deleteEvent(eventIdToDelete: String) {
        viewModelScope.launch {
            eventsRepository.deleteEvent(eventIdToDelete).fold(
                onSuccess = {
                    SnackbarManager.showMessage(R.string.event_delete_successfully)
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.event_delete_failed)
                }
            )
        }
    }

    fun getRepoEvent(calendarEvent: CalendarEvent): Event? {
        return _reposEvents.value.find { it.id == calendarEvent.id }
    }

    fun getThreeDaysEvents() {
        viewModelScope.launch {
            _threeDaysEvents.value = eventMapper.mapEvents(
                getPeriodEventsUseCase.invoke(
                    dateStart = LocalDate.now(),
                    dateEnd = LocalDate.now().plusDays(3)
                ).first()
            )
        }
    }

    fun setCalendarType(calendarType: CalendarType) {
        viewModelScope.launch {
            userDatastore.saveCalendarType(calendarType)
        }
    }
}

sealed class EventsState() {
    object Loading : EventsState()
    class Loaded(val events: List<CalendarEvent>) : EventsState()
}


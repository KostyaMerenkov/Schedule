package ru.dvfu.appliances.compose.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.event_calendar.CalendarEvent
import ru.dvfu.appliances.compose.use_cases.GetDateEventsUseCase
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.ui.Progress
import ru.dvfu.appliances.ui.ViewState
import java.time.*

class MainScreenViewModel(
    private val usersRepository: UsersRepository,
    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository,
    private val userDatastore: UserDatastore,
    private val getDateEventsUseCase: GetDateEventsUseCase,
) : ViewModel() {

    val selectedEvent = mutableStateOf<CalendarEvent?>(null)

    private val _reposEvents = MutableStateFlow<List<Event>>(listOf())

    private val _currentUser = MutableStateFlow<User>(User())
    val currentUser = _currentUser.asStateFlow()

    private val _events = MutableStateFlow<MutableList<CalendarEvent>>(mutableListOf())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val _dayEvents = MutableStateFlow<MutableList<CalendarEvent>>(mutableListOf())
    val dayEvents: StateFlow<List<CalendarEvent>> = _dayEvents.asStateFlow()

    private val appliances = MutableStateFlow<List<Appliance>>(listOf())

    init {
        getTodayEvents()
        //getAppliances()
        getEvents()
        loadCurrentUser()
        getCurrentUser()
    }

    fun getTodayEvents(date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            getDateEventsUseCase(date).collectLatest {
                _dayEvents.value = it.map { currentEvent ->
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
                }.toMutableList()
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            userDatastore.getCurrentUser.collect{

            }
        }
    }

    val mutableStateFlow: MutableStateFlow<ViewState<User>> =
        MutableStateFlow(ViewState.Loading(null))

    private fun loadCurrentUser() {
        viewModelScope.launch {
            usersRepository.currentUser
                .catch { error -> handleError(error) }
                .collectLatest { user -> user?.let { onSuccess(user) } }
        }
    }

    private fun onSuccess(user: User) {
        viewModelScope.launch {
            usersRepository.setUserListener(user)
        }
        mutableStateFlow.value = ViewState.Success(user)
    }

    private fun handleError(error: Throwable) {
        mutableStateFlow.value = ViewState.Error(error)
    }

    /*private fun getAppliances() {
        viewModelScope.launch {
            offlineRepository.getAppliances().collect {
                appliances.value = it
            }
        }
    }*/

    private fun getEvents() {
        viewModelScope.launch {
            eventsRepository.getAllEventsFromDate(LocalDate.now().toEpochDay())
                .collect { result ->
                    _reposEvents.value = result.toList()

                    _events.value = result.map { currentEvent ->
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
                    }.toMutableList()
                }
        }
    }

    private suspend fun getAppliances() {
        offlineRepository.getAppliances().collect {
            appliances.value = it
        }
    }

    fun deleteEvent(eventToDelete: CalendarEvent) {
        viewModelScope.launch {
            eventsRepository.deleteEvent(eventToDelete.id).fold(
                onSuccess = {
                    val newEventsList =
                        _events.value.filter { it.id != eventToDelete.id }.toMutableList()
                    _events.value = newEventsList
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


}
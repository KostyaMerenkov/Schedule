package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.compose.use_cases.UpdateEventUseCase
import ru.dvfu.appliances.compose.use_cases.event.EventTimeUpdateResult
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.*
import ru.dvfu.appliances.ui.ViewState
import java.time.*

class BookingListViewModel(
    private val eventsRepository: EventsRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getApplianceUseCase: GetApplianceUseCase,
    private val userDatastore: UserDatastore,
    private val updateEvent: UpdateEventUseCase
) : ViewModel() {


    private val _currentUser = MutableStateFlow<User>(User())
    val currentUser = _currentUser.asStateFlow()

    private val _viewState = MutableStateFlow<ViewState<List<CalendarEvent>>>(ViewState.Loading)
    val viewState = _viewState.asStateFlow()

//    private val _uiState = MutableStateFlow<ViewState<MutableList<CalendarEvent>>>(ViewState.Loading())
//    val uiState: StateFlow<ViewState<List<CalendarEvent>>> = _uiState.asStateFlow()

    private val _eventsList = MutableStateFlow<MutableList<CalendarEvent>>(mutableListOf())
    val eventsList: StateFlow<List<CalendarEvent>> = _eventsList.asStateFlow()

    private val appliances = MutableStateFlow<List<Appliance>>(listOf())
    private val users = MutableStateFlow<List<User>>(listOf())

    init {
        getCurrentUser()
        getEvents()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = userDatastore.getCurrentUser.first()
        }
    }

    val mutableStateFlow: MutableStateFlow<ViewState<User>> =
        MutableStateFlow(ViewState.Loading)

    private fun getEvents() {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading

            eventsRepository.getAllEvents().collect { events ->

                val calendarEvents = events
                    .map { it.toCalendarEvent(getUserUseCase, getApplianceUseCase) }
                    .sortedBy { it.date }

                _viewState.value = ViewState.Success(calendarEvents)
            }

        }
    }


    // FIXME: separate user refuse and superuser status changing
    fun manageBookStatus(
        event: CalendarEvent,
        status: BookingStatus,
        managerCommentary: String = event.managerCommentary,
        userCommentary: String = event.commentary
    ) {
        viewModelScope.launch {

            val dateAndTime = LocalDateTime.now()

            updateEvent.updateEventStatusUseCase(
                event,
                status, managerCommentary
            ).single().fold(
                onSuccess = {
                    SnackbarManager.showMessage(R.string.status_changed)
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.book_decline_failed)
                }
            )
        }
    }

    fun onUserRefuse(
        event: CalendarEvent,
        managerCommentary: String = event.managerCommentary,
    ) {
        viewModelScope.launch {
            updateEvent.updateEventStatusUseCase(
                event = event,
                newStatus = BookingStatus.DECLINED,
                managerCommentary = managerCommentary
            ).single().fold(
                onSuccess = {
                    SnackbarManager.showMessage(R.string.status_changed)
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.book_decline_failed)
                }
            )
        }
    }

    fun updateEventComment(event: CalendarEvent,  userCommentary: String) {
        viewModelScope.launch {
            updateEvent.updateUserCommentUseCase(
                event = event,
                newComment = userCommentary
            )
        }

    }

    fun updateEventDateAndTime(
        event: CalendarEvent,
        eventDateAndTime: EventDateAndTime
    ) {
        viewModelScope.launch {
            val result = updateEvent.updateTimeUseCase(
                event = event,
                eventDateAndTime
            ).single()
            when (result) {
                EventTimeUpdateResult.Error -> {
                    SnackbarManager.showMessage(R.string.error_occured)
                }
                EventTimeUpdateResult.Success -> {
                    SnackbarManager.showMessage(R.string.event_time_updated)
                }
                EventTimeUpdateResult.TimeNotFree -> {
                    SnackbarManager.showMessage(R.string.time_not_free)
                }
            }

        /*.fold(
                onSuccess = {
                    SnackbarManager.showMessage(R.string.event_time_updated)
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.book_decline_failed)
                }
            )*/
        }
    }

//    private fun couldManageBooks(user: User, book: UiBooking): Boolean {
//        return user.isAdmin() || book.appliance?.superuserIds?.contains(user.userId) == true
//    }

//    fun deleteBooking(idToDelete: String) {
//        viewModelScope.launch {
//            bookingRepository.deleteBooking(idToDelete).fold(
//                onSuccess = {
//                    val newBookingsList =
//                        _bookingList.value.filter { it.id != idToDelete }.toMutableList()
//                    _bookingList.value = newBookingsList
//                },
//                onFailure = {
//                    SnackbarManager.showMessage(R.string.event_delete_failed)
//                }
//            )
//        }
//    }

}

data class EventDateAndTime(
    val date: LocalDate,
    val timeStart: LocalTime,
    val timeEnd: LocalTime
)
package ru.dvfu.appliances.compose.viewmodels

import android.app.usage.EventStats
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.compose.use_cases.UpdateEventStatusUseCase
import ru.dvfu.appliances.compose.use_cases.UpdateEventUseCase
import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.BookingRepository
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.*
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.toLocalDate
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.ui.ViewState
import java.time.*

class BookingListViewModel(
    private val eventsRepository: EventsRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getApplianceUseCase: GetApplianceUseCase,
    private val userDatastore: UserDatastore,
    private val updateEvent: UpdateEventUseCase
) : ViewModel() {
//
//    val selectedEvent = mutableStateOf<CalendarEvent?>(null)

//    private val _reposBookingList = MutableStateFlow<List<Event>>(listOf())

    private val _currentUser = MutableStateFlow<User>(User())
    val currentUser = _currentUser.asStateFlow()

    private val _viewState = MutableStateFlow<ViewState<List<CalendarEvent>>>(ViewState.Loading())
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
        MutableStateFlow(ViewState.Loading(null))

    private fun getEvents() {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading()

            eventsRepository.getAllEvents().collect { events ->

                val calendarEvents =
                    events.map { it.toCalendarEvent(getUserUseCase, getApplianceUseCase) }

                _viewState.value = ViewState.Success(calendarEvents)
            }

        }
    }

    fun manageBookStatus(event: CalendarEvent, status: BookingStatus, commentary: String = "") {
        viewModelScope.launch {

            val dateAndTime = LocalDateTime.now()

            updateEvent(
                eventId = event.id, event = event.copy(
                    status = status,
                    managedUser = currentUser.value,
                    managerCommentary = commentary,
                    managedTime = dateAndTime
                )
            ).fold(
                onSuccess = {
                    SnackbarManager.showMessage(R.string.book_declined)
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.book_decline_failed)
                }
            )
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
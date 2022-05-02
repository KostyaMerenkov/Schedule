package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.use_cases.*
import ru.dvfu.appliances.compose.use_cases.event.EventTimeUpdateResult
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.User
import java.time.LocalDateTime

class EventInfoViewModel(
    private val eventArg: CalendarEvent,
    private val userDatastore: UserDatastore,
    private val eventsRepository: EventsRepository,
    private val updateEventUseCase: UpdateEventUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Success)
    val uiState = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            userDatastore.getCurrentUser.collect { _currentUser.value = it }
        }
    }

    private val _eventDeleteState = MutableStateFlow<UiState?>(null)
    val eventDeleteState = _eventDeleteState.asStateFlow()

    private val _event = MutableStateFlow<CalendarEvent>(eventArg)
    val event = _event.asStateFlow()

    fun deleteEvent() {
        viewModelScope.launch {
            eventsRepository.deleteEvent(eventArg).fold(
                onSuccess = {
                    _eventDeleteState.value = UiState.Success
                },
                onFailure = {
                    _eventDeleteState.value = UiState.Error
                }
            )
        }
    }

    fun onApproveClick(event: CalendarEvent, commentary: String) {
        updateEventStatus(BookingStatus.APPROVED, event, commentary)
    }

    fun onDeclineClick(event: CalendarEvent, commentary: String) {
        updateEventStatus(BookingStatus.DECLINED, event, commentary)
    }

    fun onCommentarySave(event: CalendarEvent, comment: String) {
        _uiState.value = UiState.InProgress
        viewModelScope.launch {
            updateEventUseCase.updateUserCommentUseCase(event, comment).single().fold(
                onSuccess = {
                    _event.value = _event.value.copy(commentary = comment)
                    //SnackbarManager.showMessage(comment_update_success)
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.error_occured)
                }
            )
            _uiState.value = UiState.Success
        }
    }

    fun onSetDateAndTime(event: CalendarEvent, dateAndTime: EventDateAndTime) {
        _uiState.value = UiState.InProgress
        viewModelScope.launch {
            val result = updateEventUseCase.updateTimeUseCase(event, dateAndTime).single()
            when(result) {
                EventTimeUpdateResult.Error -> {
                    SnackbarManager.showMessage(R.string.error_occured)
                }
                EventTimeUpdateResult.Success -> {
                    _event.value = _event.value.copy(
                        date = dateAndTime.date,
                        timeStart = dateAndTime.timeStart.atDate(dateAndTime.date),
                        timeEnd = dateAndTime.timeEnd.atDate(dateAndTime.date)
                    )
                    SnackbarManager.showMessage(R.string.event_time_updated)
                }
                EventTimeUpdateResult.TimeNotFree -> {
                    SnackbarManager.showMessage(R.string.time_not_free)
                }
            }
            /*.fold(
                onSuccess = {
                    _event.value = _event.value.copy(
                        date = dateAndTime.date,
                        timeStart = dateAndTime.timeStart.atDate(dateAndTime.date),
                        timeEnd = dateAndTime.timeEnd.atDate(dateAndTime.date)
                    )
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.error_occured)
                }
            )*/
            _uiState.value = UiState.Success
        }
    }

    private fun updateEventStatus(
        bookingStatus: BookingStatus,
        event: CalendarEvent,
        managerCommentary: String,
    ) {
        _uiState.value = UiState.InProgress
        viewModelScope.launch {
            updateEventUseCase.updateEventStatusUseCase.invoke(
                event,
                bookingStatus,
                managerCommentary,
            ).first().fold(
                onSuccess = {
                    _event.value = _event.value.copy(
                        status = bookingStatus,
                        managerCommentary = managerCommentary,
                        managedUser = currentUser.value,
                        managedTime = LocalDateTime.now(),
                    )
                    SnackbarManager.showMessage(R.string.status_changed)
                    _uiState.value = UiState.Success
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.change_status_failed)
                    _uiState.value = UiState.Error
                }
            )
        }
    }

}
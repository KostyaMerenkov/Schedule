package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.use_cases.*
import ru.dvfu.appliances.compose.utils.AvailabilityState
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.TimeConstants.MIN_EVENT_DURATION
import ru.dvfu.appliances.model.utils.toMillis
import ru.dvfu.appliances.ui.ViewState
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EventInfoViewModel(
    private val eventArg: CalendarEvent,
    private val userDatastore: UserDatastore,
    private val eventsRepository: EventsRepository,
    private val getApplianceUseCase: GetApplianceUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getEventNewTimeEndAvailabilityUseCase: GetEventNewTimeEndAvailabilityUseCase,
    private val updateEventStatusUseCase: UpdateEventStatusUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Success)
    val uiState = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    private val _userState = MutableStateFlow<ViewState<User>>(ViewState.Loading)
    val userState = _userState.asStateFlow()

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            userDatastore.getCurrentUser.collect { _currentUser.value = it }
        }
    }

    private val _timeStartChangeState = MutableStateFlow<UiState?>(null)
    val timeStartChangeState = _timeStartChangeState.asStateFlow()

    private val _timeEndChangeState = MutableStateFlow<UiState?>(null)
    val timeEndChangeState = _timeEndChangeState.asStateFlow()

    private val _eventDeleteState = MutableStateFlow<UiState?>(null)
    val eventDeleteState = _eventDeleteState.asStateFlow()

    private val _event = MutableStateFlow<CalendarEvent>(eventArg)
    val event = _event.asStateFlow()

    val canUpdate: MutableStateFlow<Boolean>
        get() = MutableStateFlow(event.value != eventArg)

    private val _appliance = MutableStateFlow(Appliance())
    private val _applianceState = MutableStateFlow<ViewState<Appliance>>(ViewState.Loading)
    val applianceState = _applianceState.asStateFlow()

    private val _superUserState = MutableStateFlow<ViewState<User>>(ViewState.Loading)
    val superUserState = _superUserState.asStateFlow()


    val couldDeleteEvent: MutableStateFlow<Boolean>
        get() = MutableStateFlow(
            currentUser.value.isAdmin()
                    || _appliance.value.superuserIds.contains(currentUser.value.userId)
        )

    val couldEditTimeEnd: MutableStateFlow<Boolean>
        get() = MutableStateFlow<Boolean>(
            couldDeleteEvent.value ||
                    Duration.between(event.value.timeEnd, LocalDateTime.now()) > MIN_EVENT_DURATION
                    && _appliance.value.superuserIds.contains(currentUser.value.userId)
        )


    val couldEditTimeStart: MutableStateFlow<Boolean>
        get() = MutableStateFlow(
            couldDeleteEvent.value ||
                    Duration.between(
                        event.value.timeStart,
                        LocalDateTime.now()
                    ) < MIN_EVENT_DURATION
                    && _appliance.value.superuserIds.contains(currentUser.value.userId)
        )


    private fun getSuperUser(superUserId: String?) {
        superUserId?.let {
            viewModelScope.launch {
                getUserUseCase.invoke(superUserId).collect {
                    it.fold(
                        onSuccess = {
                            _superUserState.value = ViewState.Success(it)
                        },
                        onFailure = {
                            _superUserState.value = ViewState.Error(it)
                        }
                    )
                }
            }
        }
    }

    private fun getUser(userId: String) {
        viewModelScope.launch {
            getUserUseCase.invoke(userId).collect {
                it.fold(
                    onSuccess = {
                        _userState.value = ViewState.Success(it)
                    },
                    onFailure = {
                        _userState.value = ViewState.Error(it)
                    }
                )
            }
        }
    }

    private fun getAppliance(applianceId: String) {
        viewModelScope.launch {
            getApplianceUseCase.invoke(applianceId).collect {
                it.fold(
                    onSuccess = {
                        _applianceState.value = ViewState.Success(it)
                        _appliance.value = it
                    },
                    onFailure = {
                        _applianceState.value = ViewState.Error(it)
                    }
                )
            }
        }
    }

    fun saveChanges() {

    }

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

    fun onTimeEndChange(newTime: LocalTime) {
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
    }

    fun onTimeStartChange(newTime: LocalTime) {
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
    }

    fun onApproveClick(event: CalendarEvent, commentary: String) {
        updateEventStatus(BookingStatus.APPROVED, event)
    }

    fun onDeclineClick(event: CalendarEvent, commentary: String) {
        updateEventStatus(BookingStatus.DECLINED, event)
    }

    fun onCommentarySave(event: CalendarEvent, comment: String) {
        _uiState.value = UiState.InProgress
        viewModelScope.launch {

            //updateEventUseCase(event)
            _uiState.value = UiState.Success
        }
    }

    private fun updateEventStatus(
        bookingStatus: BookingStatus,
        event: CalendarEvent
    ) {
        _uiState.value = UiState.InProgress
        viewModelScope.launch {
            updateEventStatusUseCase(event, bookingStatus).first().fold(
                onSuccess = {
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
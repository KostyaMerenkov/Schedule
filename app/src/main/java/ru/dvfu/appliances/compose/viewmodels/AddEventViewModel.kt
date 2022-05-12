package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.BuildConfig
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.use_cases.GetAppliancesUseCase
import ru.dvfu.appliances.compose.use_cases.GetEventTimeAvailabilityUseCase
import ru.dvfu.appliances.compose.utils.AvailabilityState
import ru.dvfu.appliances.compose.utils.NotificationManager
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.*
import ru.dvfu.appliances.model.utils.TimeConstants.DEFAULT_EVENT_DURATION
import ru.dvfu.appliances.model.utils.TimeConstants.MIN_EVENT_DURATION
import ru.dvfu.appliances.model.utils.toMillis
import ru.dvfu.appliances.ui.ViewState
import java.time.*
import java.util.*


class AddEventViewModel(
    private val selectedDate: LocalDate,
    private val eventsRepository: EventsRepository,
    private val getAppliancesUseCase: GetAppliancesUseCase,
    private val getEventTimeAvailabilityUseCase: GetEventTimeAvailabilityUseCase,
    private val userDatastore: UserDatastore,
    private val notificationManager: NotificationManager,
) : ViewModel() {

    private val _selectedAppliance = MutableStateFlow<Appliance?>(null)
    val selectedAppliance = _selectedAppliance.asStateFlow()

    private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _appliancesState = MutableStateFlow<ViewState<List<Appliance>>>(ViewState.Loading)
    val appliancesState = _appliancesState.asStateFlow()

    private val currentUser = MutableStateFlow(User())

    val date = mutableStateOf(selectedDate)
    val timeStart = mutableStateOf<LocalDateTime>(
        if (selectedDate.isAfter(LocalDate.now())) selectedDate.atTime(8, 0)
        else LocalTime.now().atDate(selectedDate)
    )
    val timeEnd = mutableStateOf<LocalDateTime>(timeStart.value.plus(DEFAULT_EVENT_DURATION))
    val commentary = mutableStateOf("")

    val isDurationError: MutableStateFlow<Boolean>
        get() = MutableStateFlow(
            timeEnd.value.isBefore(timeStart.value) || Duration.between(
                timeStart.value, timeEnd.value,
            ) < MIN_EVENT_DURATION
        )
    val duration: MutableStateFlow<String>
        get() {
            val dur = Duration.between(timeStart.value, timeEnd.value)
            val period = String.format(
                Locale.getDefault(), "%02d:%02d", dur.toHours(),
                dur.minusHours(dur.toHours()).toMinutes(),
            )
            return MutableStateFlow(period)
        }

    init {
        getCurrentUser()
        loadAppliances()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            userDatastore.getCurrentUser.collect {
                currentUser.value = it
            }
        }
    }

    private fun loadAppliances() {
        viewModelScope.launch {
            getAppliancesUseCase.invoke().collect { result ->
                val appliances = result.getOrDefault(listOf()).filter { it.active }
                _appliancesState.value = ViewState.Success(appliances)
            }
        }
    }

    fun addEvent() {
        _uiState.value = UiState.InProgress
        viewModelScope.launch {
            val selectedAppliance = selectedAppliance.value
            if (isDurationError.value || selectedAppliance == null) {
                showError()
            } else {
                val availabilityResult = getEventTimeAvailabilityUseCase.invoke(
                    selectedAppliance.id,
                    eventDateAndTime = EventDateAndTime(
                        timeStart = timeStart.value.toLocalTime(),
                        timeEnd = timeEnd.value.toLocalTime(),
                        date = date.value
                    )
                ).single()

                when (availabilityResult) {
                    AvailabilityState.Available -> addNewEvent(
                        if (selectedAppliance.isUserSuperuserOrAdmin(currentUser.value))
                            prepareApprovedEvent(selectedAppliance)
                        else prepareEvent(selectedAppliance)

                    )
                    AvailabilityState.Error -> {
                        _uiState.value = UiState.Error
                        SnackbarManager.showMessage(R.string.new_event_failed)
                    }
                    AvailabilityState.NotAvailable -> {
                        _uiState.value = UiState.Error
                        SnackbarManager.showMessage(R.string.time_not_free)
                    }
                }
            }
        }
    }

    private fun prepareEvent(selectedAppliance: Appliance): Event {
        return Event(
            date = date.value.toMillis,
            timeCreated = LocalDateTime.now().toMillis,
            timeStart = timeStart.value.toMillis,
            timeEnd = timeEnd.value.toMillis,
            commentary = commentary.value,
            applianceId = selectedAppliance.id,
            userId = currentUser.value.userId,
            status = BookingStatus.NONE,
        )
    }

    private fun prepareApprovedEvent(selectedAppliance: Appliance): Event {
        return Event(
            date = date.value.toMillis,
            timeCreated = LocalDateTime.now().toMillis,
            timeStart = timeStart.value.toMillis,
            timeEnd = timeEnd.value.toMillis,
            commentary = commentary.value,
            applianceId = selectedAppliance.id,
            userId = currentUser.value.userId,
            managedTime = LocalDateTime.now().toMillis,
            managerCommentary = "",
            managedById = currentUser.value.userId,
            status = BookingStatus.APPROVED,
        )
    }

    private fun addNewEvent(event: Event) {
        viewModelScope.launch {
            eventsRepository.addNewEvent(event).fold(
                onSuccess = {
                    notificationManager.newEvent(event)
                    SnackbarManager.showMessage(R.string.add_event_success)
                    _uiState.value = UiState.Success
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.add_event_failed)
                    _uiState.value = UiState.Error
                }
            )
        }
    }

    private fun showError() {
        when {
            Duration.between(timeStart.value, timeEnd.value) < MIN_EVENT_DURATION -> {
                SnackbarManager.showMessage(R.string.duration_error)
            }
            selectedAppliance.value == null -> {
                SnackbarManager.showMessage(R.string.appliance_not_chosen)
            }
            else -> SnackbarManager.showMessage(R.string.error_occured)
        }
        _uiState.value = UiState.Error
    }

    fun onApplianceSelected(appliance: Appliance) {
        _selectedAppliance.value = appliance.takeIf { it != _selectedAppliance.value }
    }

    fun onCommentarySet(commentary: String) {
        this.commentary.value = commentary
    }

    fun onDateSet(date: LocalDate) {
        if (BuildConfig.DEBUG && date.isBefore(LocalDate.now())) {
            SnackbarManager.showMessage(R.string.past_day_error)
            return
        }
        this.date.value = LocalDate.of(date.year, date.month, date.dayOfMonth)
        if (date.isAfter(LocalDate.now())) {
            timeStart.value = date.atTime(8, 0)
            timeEnd.value = timeStart.value.plus(DEFAULT_EVENT_DURATION)
        } else {
            timeStart.value = date.atTime(LocalTime.now())
            timeEnd.value = timeStart.value.plus(DEFAULT_EVENT_DURATION)
        }
    }

    fun onTimeStartSet(time: LocalTime) {
        timeStart.value = date.value.atTime(time.hour, time.minute)
    }

    fun onTimeEndSet(time: LocalTime) {
        timeEnd.value = date.value.atTime(time.hour, time.minute)
    }

}
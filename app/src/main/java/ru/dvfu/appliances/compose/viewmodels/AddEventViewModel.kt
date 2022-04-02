package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.use_cases.GetEventNewTimeEndAvailabilityUseCase
import ru.dvfu.appliances.compose.use_cases.GetAppliancesUseCase
import ru.dvfu.appliances.compose.use_cases.GetNewEventTimeAvailabilityUseCase
import ru.dvfu.appliances.compose.utils.AvailabilityState
import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.randomUUID
import ru.dvfu.appliances.ui.ViewState
import java.time.*
import java.util.*

class AddEventViewModel(
    private val eventsRepository: EventsRepository,
    private val getAppliancesUseCase: GetAppliancesUseCase,
    private val getNewEventTimeAvailabilityUseCase: GetNewEventTimeAvailabilityUseCase,
    private val userDatastore: UserDatastore,
) : ViewModel() {

    private val _selectedAppliance = MutableStateFlow<Appliance?>(null)
    val selectedAppliance = _selectedAppliance.asStateFlow()

    private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _appliancesState = MutableStateFlow<ViewState<List<Appliance>>>(ViewState.Loading())
    val appliancesState = _appliancesState.asStateFlow()

    val date = mutableStateOf(LocalDate.now())
    val timeStart = mutableStateOf<LocalDateTime>(LocalDateTime.now())
    val timeEnd = mutableStateOf<LocalDateTime>(LocalDateTime.now())
    val commentary = mutableStateOf("")

    val isDurationError: MutableStateFlow<Boolean>
        get() = MutableStateFlow(
            timeEnd.value.isBefore(timeStart.value) || Duration.between(
                timeStart.value, timeEnd.value,
            ) < Duration.ofMinutes(10)
        )
    val duration: MutableStateFlow<String>
        get() {
            val dur = Duration.between(timeStart.value, timeEnd.value)

            val period = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                dur.toHours(),
                dur.minusHours(dur.toHours()).toMinutes(),
            )
            return MutableStateFlow(period)
        }

    private val isError: MutableStateFlow<Boolean>
        get() = MutableStateFlow(isDurationError.value || selectedAppliance.value == null)

    init {
        loadAppliances()
    }

    private fun loadAppliances() {
        viewModelScope.launch {
            getAppliancesUseCase.invoke().collect { appliances ->
                _appliancesState.value = ViewState.Success(appliances)
            }
        }
    }

    fun addEvent() {
        val selectedAppliance = _selectedAppliance.value
        viewModelScope.launch {
            if (isError.value) {
                showError()
            } else {
                selectedAppliance?.let {
                    _uiState.value = UiState.InProgress
                    getNewEventTimeAvailabilityUseCase(
                        it.id,
                        timeStart.value.toMillis,
                        timeEnd.value.toMillis
                    ).collect { result ->
                        when (result) {
                            AvailabilityState.Available -> addNewEvent(selectedAppliance)
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
        }
    }

    private fun addNewEvent(appliance: Appliance) {
        viewModelScope.launch {
            eventsRepository.addNewEvent(
                Event(
                    id = randomUUID(),
                    timeCreated = LocalDateTime.now().toMillis,
                    timeStart = timeStart.value.toMillis,
                    timeEnd = timeEnd.value.toMillis,
                    commentary = commentary.value,
                    applianceId = appliance.id,
                    applianceName = appliance.name,
                    color = appliance.color,
                    userId = userDatastore.getCurrentUser.first<User>().userId,
                )
            ).fold(
                onSuccess = {
                    _uiState.value = UiState.Success
                },
                onFailure = {
                    _uiState.value = UiState.Error
                }
            )

        }
    }

    private fun showError() {
        when {
            Duration.between(
                timeStart.value, timeEnd.value,
            ) < Duration.ofMinutes(10) -> {
                SnackbarManager.showMessage(R.string.duration_error)
            }
            selectedAppliance.value == null -> {
                SnackbarManager.showMessage(R.string.appliance_not_chosen)
            }
            else -> SnackbarManager.showMessage(R.string.error_occured)
        }
    }

    fun onApplianceSelected(appliance: Appliance) {
        _selectedAppliance.value = appliance.takeIf { it != _selectedAppliance.value }
    }

    fun onCommentarySet(commentary: String) {
        this.commentary.value = commentary
    }

    fun onDateSet(date: LocalDate) {
        this.date.value = LocalDate.of(date.year, date.month, date.dayOfMonth)
        timeStart.value = date.atTime(timeStart.value.hour, timeStart.value.minute)
        timeEnd.value = date.atTime(timeEnd.value.hour, timeEnd.value.minute)
    }

    fun onTimeStartSet(time: LocalTime) {
        timeStart.value = date.value.atTime(time.hour, time.minute)
    }

    fun onTimeEndSet(time: LocalTime) {
        timeEnd.value = date.value.atTime(time.hour, time.minute)
    }

}
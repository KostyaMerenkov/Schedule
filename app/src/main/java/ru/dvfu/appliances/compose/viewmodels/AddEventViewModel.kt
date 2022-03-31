package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.randomUUID
import ru.dvfu.appliances.ui.Progress
import ru.dvfu.appliances.ui.ViewState
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.toKotlinDuration

class AddEventViewModel(
    private val usersRepository: UsersRepository,
    private val appliancesRepository: AppliancesRepository,
    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository,
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
        get() = MutableStateFlow(timeEnd.value.isBefore(timeStart.value))
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
        loadAppliancesOffline()
    }

    private fun loadAppliancesOffline() {
        viewModelScope.launch {
            offlineRepository.getAppliances().collect { appliances ->
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
                    eventsRepository.addNewEvent(
                        Event(
                            id = randomUUID(),
                            timeStart = timeStart.value,
                            timeEnd = timeEnd.value,
                            commentary = commentary.value,
                            applianceId = it.id,
                            applianceName = it.name,
                            color = it.color,
                            userId = usersRepository.currentUser.first()!!.userId
                        )
                    ).collect { progress ->
                        when (progress) {
                            is Progress.Complete -> {
                                _uiState.value = UiState.Success
                            }
                            is Progress.Loading -> {
                                _uiState.value = UiState.InProgress
                            }
                            is Progress.Error -> {
                                _uiState.value = UiState.Error
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showError() {
        when {
            Duration.between(timeEnd.value, timeStart.value) < Duration.ofMinutes(10) /*TimeUnit.MILLISECONDS.toMinutes(10)*/ -> {
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
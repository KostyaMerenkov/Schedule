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
import java.util.*
import java.util.concurrent.TimeUnit

class AddEventViewModel(
    private val usersRepository: UsersRepository,
    private val appliancesRepository: AppliancesRepository,
    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository,
) : ViewModel() {

    private val _selectedAppliance = MutableStateFlow<Appliance?>(null)
    val selectedAppliance = _selectedAppliance.asStateFlow()

    val isRefreshing = mutableStateOf<Boolean>(false)

    private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _appliancesState = MutableStateFlow<ViewState<List<Appliance>>>(ViewState.Loading())
    val appliancesState = _appliancesState.asStateFlow()

    val date = mutableStateOf(0L)
    val timeStart = mutableStateOf(0L)
    val timeEnd = mutableStateOf(0L)
    val commentary = mutableStateOf("")

    val isDurationError: MutableStateFlow<Boolean>
        get() = MutableStateFlow(TimeUnit.MILLISECONDS.toMinutes(timeEnd.value - timeStart.value) < 0)
    val duration: MutableStateFlow<String>
        get() {
            /*       LocalDateTime
                   ChronoUnit.MINUTES.between(calendarEvent.start, calendarEvent.end)*/
            val mills = timeEnd.value - timeStart.value
            val period = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(mills),
                TimeUnit.MILLISECONDS.toMinutes(mills) % TimeUnit.HOURS.toMinutes(1)
            )
            return MutableStateFlow(period)
        }

    val isError: MutableStateFlow<Boolean>
        get() {
            return MutableStateFlow<Boolean>(
                isDurationError.value || selectedAppliance.value == null
            )
        }

    init {
        loadAppliancesOffline()
    }

    private fun loadAppliancesOffline() {
        isRefreshing.value = true
        viewModelScope.launch {
            offlineRepository.getAppliances().collect { appliances ->
                _appliancesState.value = ViewState.Success(appliances)
                isRefreshing.value = false
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
            TimeUnit.MILLISECONDS.toMinutes(timeEnd.value - timeStart.value) < TimeUnit.MILLISECONDS.toMinutes(
                10
            ) -> {
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

    fun onDateSet(date: Long) {
        val calendarToSet = Calendar.getInstance().apply { timeInMillis = date }
        timeStart.value = Calendar.getInstance().apply {
            timeInMillis = timeStart.value
            set(Calendar.YEAR, calendarToSet.get(Calendar.YEAR))
            set(Calendar.MONTH, calendarToSet.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, calendarToSet.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis

        timeEnd.value = Calendar.getInstance().apply {
            timeInMillis = timeEnd.value
            set(Calendar.YEAR, calendarToSet.get(Calendar.YEAR))
            set(Calendar.MONTH, calendarToSet.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, calendarToSet.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }

}
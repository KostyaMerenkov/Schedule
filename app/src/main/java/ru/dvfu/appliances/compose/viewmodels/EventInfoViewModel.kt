package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.randomUUID
import ru.dvfu.appliances.ui.Progress
import ru.dvfu.appliances.ui.ViewState
import java.util.*
import java.util.concurrent.TimeUnit

class EventInfoViewModel(
    private val event: Event,
    private val usersRepository: UsersRepository,
    private val appliancesRepository: AppliancesRepository,
    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository,
    private val getApplianceUseCase: GetApplianceUseCase,
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    init {
        getAppliance(event.applianceId)
        getUser(event.userId)
        getSuperUser(event.superUserId)
    }

    private val _applianceState = MutableStateFlow<ViewState<Appliance>>(ViewState.Loading())
    val applianceState = _applianceState.asStateFlow()

    private val _userState = MutableStateFlow<ViewState<User>>(ViewState.Loading())
    val userState = _userState.asStateFlow()

    private fun getSuperUser(superUserId: String?) {
        superUserId?.let {
            viewModelScope.launch {
                getUserUseCase.invoke(superUserId).collect {
                    it.fold(
                        onSuccess = {
                            //_superUserState.value = ViewState.Success(it)
                        },
                        onFailure = {
                            //_superUserState.value = ViewState.Error(it)
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
                    },
                    onFailure = {
                        _applianceState.value = ViewState.Error(it)
                    }
                )
            }
        }
    }

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
        if (isError.value) {
            showError()
        } else {
            viewModelScope.launch {
                eventsRepository.addNewEvent(
                    Event(
                        id = randomUUID(),
                        timeStart = timeStart.value,
                        timeEnd = timeEnd.value,
                        commentary = commentary.value,
                        applianceId = _selectedAppliance.value!!.id,
                        userId = usersRepository.currentUser.single()!!.userId
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

    private fun showError() {
        when {
            TimeUnit.MILLISECONDS.toMinutes(timeEnd.value - timeStart.value) < TimeUnit.MILLISECONDS.toMinutes(10) -> {
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

}
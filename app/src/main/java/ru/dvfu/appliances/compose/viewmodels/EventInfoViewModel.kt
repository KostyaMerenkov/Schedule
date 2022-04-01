package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.boguszpawlowski.composecalendar.Calendar
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.compose.utils.TimeConstants.MILLISECONDS_IN_MINUTE
import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.toLocalDate
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.model.utils.toLocalTime
import ru.dvfu.appliances.ui.ViewState
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime

class EventInfoViewModel(
    private val eventArg: Event,
    private val userDatastore: UserDatastore,
    private val appliancesRepository: AppliancesRepository,
    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository,
    private val getApplianceUseCase: GetApplianceUseCase,
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    private val currentUser = MutableStateFlow(User())

    init {
        getCurrentUser()
        getAppliance(eventArg.applianceId)
        getUser(eventArg.userId)
        getSuperUser(eventArg.superUserId)
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            currentUser.value = userDatastore.getCurrentUser.first()
        }
    }

    private val _timeChangeState = MutableStateFlow<UiState?>(null)
    val timeChangeState = _timeChangeState.asStateFlow()


    private val _eventDeleteState = MutableStateFlow<UiState?>(null)
    val eventDeleteState = _eventDeleteState.asStateFlow()

    private val _event = MutableStateFlow(eventArg)
    val event = _event.asStateFlow()

    val canUpdate: MutableStateFlow<Boolean>
        get() = MutableStateFlow(event.value != eventArg)

    private val _appliance = MutableStateFlow(Appliance())
    private val _applianceState = MutableStateFlow<ViewState<Appliance>>(ViewState.Loading())
    val applianceState = _applianceState.asStateFlow()

    private val _userState = MutableStateFlow<ViewState<User>>(ViewState.Loading())
    val userState = _userState.asStateFlow()

    private val _superUserState = MutableStateFlow<ViewState<User>>(ViewState.Loading())
    val superUserState = _superUserState.asStateFlow()


    val couldDeleteEvent: MutableStateFlow<Boolean>
        get() = MutableStateFlow(
            currentUser.value.isAdmin()
                    || _appliance.value.superuserIds.contains(currentUser.value.userId)
        )

    val couldEditTimeEnd: MutableStateFlow<Boolean>
        get() = MutableStateFlow(
            couldDeleteEvent.value ||
                    ((event.value.timeEnd - System.currentTimeMillis()) < (MILLISECONDS_IN_MINUTE * 10)
                            && _appliance.value.userIds.contains(currentUser.value.userId))

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
            eventsRepository.deleteEvent(eventArg.id).fold(
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
            _timeChangeState.value = UiState.InProgress
            val oldDate = event.value.timeEnd.toLocalDate()
            val oldTime = event.value.timeEnd.toLocalTime()
            val newTimeMillis = newTime.atDate(oldDate).toMillis
            if (oldTime.isAfter(newTime)) {
                when {
                    newTime.isBefore(LocalTime.now()) -> {
                        _timeChangeState.value = UiState.Error
                        SnackbarManager.showMessage(R.string.time_end_is_before_now)
                    }
                    newTime.isBefore(event.value.timeEnd.toLocalTime()) -> {
                        SnackbarManager.showMessage(R.string.time_end_is_before_start)
                    }
                    else -> saveNewTimeEnd(newTimeMillis)
                }
            } else {
                eventsRepository.getActiveApplianceEvents(_appliance.value.id, newTimeMillis).fold(
                    onSuccess = {
                        if (it.isEmpty()) saveNewTimeEnd(newTimeMillis)
                        else {
                            if (isTimeFree(
                                    list = it.filter { it.id != event.value.id },
                                    time = newTimeMillis
                                )
                            )
                                saveNewTimeEnd(newTimeMillis)
                            else {
                                _timeChangeState.value = UiState.Error
                                SnackbarManager.showMessage(R.string.time_not_free)
                            }
                        }
                    },
                    onFailure = {
                        _timeChangeState.value = UiState.Error
                        SnackbarManager.showMessage(R.string.new_event_time_end_failed)
                    }
                )
            }
        }
    }

    private fun isTimeFree(list: List<Event>, time: Long): Boolean {
        list.forEach {
            if (it.timeStart < time) return false
        }
        return true
    }

    private fun saveNewTimeEnd(newTime: Long) {
        viewModelScope.launch {
            eventsRepository.setNewTimeEnd(
                eventId = event.value.id,
                timeEnd = newTime
            ).fold(
                onSuccess = {
                    _timeChangeState.value = UiState.Success
                    _event.value = event.value.copy(timeEnd = newTime)
                },
                onFailure = {
                    _timeChangeState.value = UiState.Error
                    SnackbarManager.showMessage(R.string.new_event_time_end_failed)
                }
            )
        }

    }


}
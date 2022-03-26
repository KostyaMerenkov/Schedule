package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
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
import ru.dvfu.appliances.ui.ViewState

class EventInfoViewModel(
    private val eventArg: Event,
    private val usersRepository: UsersRepository,
    private val appliancesRepository: AppliancesRepository,
    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository,
    private val getApplianceUseCase: GetApplianceUseCase,
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    init {
        getAppliance(eventArg.applianceId)
        getUser(eventArg.userId)
        getSuperUser(eventArg.superUserId)
    }

    /*private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState = _uiState.asStateFlow()*/

    private val _eventDeleteState = MutableStateFlow<UiState?>(null)
    val eventDeleteState = _eventDeleteState.asStateFlow()

    private val _event = MutableStateFlow(eventArg)
    val event = _event.asStateFlow()

    val canUpdate: MutableStateFlow<Boolean>
        get() = MutableStateFlow(event.value != eventArg)

    private val _applianceState = MutableStateFlow<ViewState<Appliance>>(ViewState.Loading())
    val applianceState = _applianceState.asStateFlow()

    private val _userState = MutableStateFlow<ViewState<User>>(ViewState.Loading())
    val userState = _userState.asStateFlow()

    private val _superUserState = MutableStateFlow<ViewState<User>>(ViewState.Loading())
    val superUserState = _superUserState.asStateFlow()

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
            eventsRepository.deleteEvent(eventArg.id).collect{
                it.fold(
                    onSuccess = {
                        _eventDeleteState.value = UiState.Success
                    },
                    onFailure = {
                        _eventDeleteState.value = UiState.Error
                    }
                )
            }
        }
    }


}
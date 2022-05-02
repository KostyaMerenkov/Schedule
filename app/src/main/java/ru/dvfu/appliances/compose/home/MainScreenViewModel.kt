package ru.dvfu.appliances.compose.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetDateEventsUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.compose.utils.EventMapper
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.*
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.toLocalDate
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.ui.ViewState
import java.time.*

class MainScreenViewModel(
    private val usersRepository: UsersRepository,
    private val userDatastore: UserDatastore,
    private val appliancesRepository: AppliancesRepository,
) : ViewModel() {


    private val _currentUser = MutableStateFlow<User>(User())
    val currentUser = _currentUser.asStateFlow()

    private val _events = MutableStateFlow<MutableList<CalendarEvent>>(mutableListOf())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val _dayEvents = MutableStateFlow<MutableList<CalendarEvent>>(mutableListOf())
    val dayEvents: StateFlow<List<CalendarEvent>> = _dayEvents.asStateFlow()

    private val appliances = MutableStateFlow<List<Appliance>>(listOf())

    init {
        getAppliances()
        loadCurrentUser()
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            userDatastore.getCurrentUser.collect{
                _currentUser.value = it
            }
        }
    }

    val mutableStateFlow: MutableStateFlow<ViewState<User>> =
        MutableStateFlow(ViewState.Loading)

    private fun loadCurrentUser() {
        viewModelScope.launch {
            usersRepository.currentUser
                .catch { error -> handleError(error) }
                .collectLatest { user -> user?.let { onSuccess(user) } }
        }
    }

    private fun onSuccess(user: User) {
        if (user.anonymous.not()) {
            viewModelScope.launch {
                usersRepository.setUserListener(user)
            }
        }
        mutableStateFlow.value = ViewState.Success(user)
    }

    private fun handleError(error: Throwable) {
        mutableStateFlow.value = ViewState.Error(error)
    }

    private fun getAppliances() {
        viewModelScope.launch {
            appliancesRepository.getAppliances().collect {
                appliances.value = it
            }
        }
    }

}
package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.Progress

class AddUserViewModel(
    private val areSuperUsers: Boolean,
    private val appliance: Appliance,
    private val repository: Repository,
) : ViewModel() {

    val usersList = MutableStateFlow(listOf<User>())
    val isRefreshing = mutableStateOf<Boolean>(false)

    init {
        loadUsers()
    }

    private val _uiState = MutableStateFlow<BaseViewState>(BaseViewState.Success(null))
    val uiState: StateFlow<BaseViewState>
        get() = _uiState

    fun refresh() = loadUsers()

    private fun loadUsers() {
        isRefreshing.value = true
        viewModelScope.launch {
            repository.getUsers().collect { users ->
                delay(1000)
                usersList.value = users as List<User>
                isRefreshing.value = false
            }
        }
    }

    fun addToAppliance(appliance: Appliance, selectedUsers: MutableList<User>) {
        if (selectedUsers.isEmpty()) SnackbarManager.showMessage(R.string.no_users_chosen)
        else {
            when (areSuperUsers) {
                true -> {
                    val usersToAdd = selectedUsers
                        .map { it.userId }.toMutableList()
                        .apply { addAll(appliance.superuserIds) }
                        .distinct()
                    addSuperUsersToAppliance(appliance, usersToAdd)
                }
                false -> {
                    val usersToAdd = selectedUsers
                        .map { it.userId }.toMutableList()
                        .apply { addAll(appliance.userIds) }
                        .distinct()
                    addUsersToAppliance(appliance, usersToAdd)
                }
            }
        }

    }

    private fun addUsersToAppliance(appliance: Appliance, selectedUsers: List<String>) {
        _uiState.value = BaseViewState.Loading(0)

        viewModelScope.launch {
            repository.addUsersToAppliance(appliance, selectedUsers).collect { progress ->
                when (progress) {
                    is Progress.Complete -> {
                        _uiState.value = BaseViewState.Success(progress)
                    }
                    is Progress.Loading -> {
                        _uiState.value = BaseViewState.Loading(progress.percents)
                    }
                    is Progress.Error -> {
                        _uiState.value =
                            BaseViewState.Error(progress.error)
                    }
                }
            }
        }

    }

    private fun addSuperUsersToAppliance(appliance: Appliance, selectedSuperUsers: List<String>) {
        _uiState.value = BaseViewState.Loading(0)

        viewModelScope.launch {
            repository.addSuperUsersToAppliance(appliance, selectedSuperUsers).collect { progress ->
                when (progress) {
                    is Progress.Complete -> {
                        _uiState.value = BaseViewState.Success(progress)
                    }
                    is Progress.Loading -> {
                        _uiState.value = BaseViewState.Loading(progress.percents)
                    }
                    is Progress.Error -> {
                        _uiState.value =
                            BaseViewState.Error(progress.error)
                    }
                }
            }
        }
    }

}
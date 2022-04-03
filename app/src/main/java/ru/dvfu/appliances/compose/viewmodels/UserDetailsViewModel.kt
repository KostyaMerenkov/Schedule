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
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.repository.entity.User

class UserDetailsViewModel(
    detUser: User,
    private val usersRepository: UsersRepository,
    private val repository: AppliancesRepository,
    private val userDatastore: UserDatastore
) : ViewModel() {

    val currentUser: MutableStateFlow<User> = MutableStateFlow(User())
    val detailsUser: MutableStateFlow<User> = MutableStateFlow(User())

    private val _userRoleState = MutableStateFlow<UiState>(UiState.Success)
    val userRoleState = _userRoleState.asStateFlow()

    init {
        getCurrentUser()
        setDetailsUser(detUser)
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            userDatastore.getCurrentUser.collect{
                currentUser.value = it
            }
        }
    }


    val currentUserAppliances = MutableStateFlow<List<Appliance>?>(null)
    val currentSuperUserAppliances = MutableStateFlow<List<Appliance>?>(null)

    fun setDetailsUser(user: User) {
        detailsUser.value = user
        getUserAppliances(user);
        getSuperUserAppliances(user)
    }


    private fun getSuperUserAppliances(user: User) {
        viewModelScope.launch {
            repository.getSuperUserAppliances(user.userId).collect {
                currentSuperUserAppliances.value = it
            }
        }
    }

    private fun getUserAppliances(user: User) {
        viewModelScope.launch {
            repository.getUserAppliances(user.userId).collect {
                currentUserAppliances.value = it
            }
        }
    }

    fun updateUserRole(user: User, ordinal: Int) {
        viewModelScope.launch {
            _userRoleState.value = UiState.InProgress
            usersRepository.updateUserField(user.userId, mapOf("role" to ordinal)).fold(
                onSuccess = {
                    _userRoleState.value = UiState.Success
                    SnackbarManager.showMessage(R.string.role_changed_successfully)
                    detailsUser.value = detailsUser.value.copy(role = ordinal)
                },
                onFailure = {
                    _userRoleState.value = UiState.Error
                }
            )
        }
    }


}
package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.repository.entity.User

class UserDetailsViewModel(
    private val usersRepository: UsersRepository,
    private val repository: AppliancesRepository,
) : ViewModel() {

    companion object {
        val defUser = User()
    }

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            usersRepository.currentUserFromDB.collect {
                currentUser.value = it
            }
        }
    }

    val currentUser: MutableStateFlow<User> = MutableStateFlow(defUser)
    val detailsUser: MutableStateFlow<User> = MutableStateFlow(defUser)

    val currentUserAppliances = MutableStateFlow<List<Appliance>?>(null)
    val currentSuperUserAppliances = MutableStateFlow<List<Appliance>?>(null)

    fun setDetailsUser(user: User) {
        if (detailsUser.value == defUser) {
            detailsUser.value = user
            updateUser()
            when(user.role) {
                Roles.USER.ordinal -> { getUserAppliances(user) }
                Roles.ADMIN.ordinal -> {
                    getUserAppliances(user);
                    getSuperUserAppliances(user) }
            }
        }
    }

    private fun updateUser() {
        viewModelScope.launch {
            usersRepository.getUserWithId(detailsUser.value.userId).collect {
                detailsUser.value = it
            }
        }
    }

    fun getSuperUserAppliances(user: User) {
        viewModelScope.launch {
            repository.getSuperUserAppliances(user.userId).collect {
                currentSuperUserAppliances.value = it
            }
        }
    }

    fun getUserAppliances(user: User) {
        viewModelScope.launch {
            repository.getUserAppliances(user.userId).collect {
                currentUserAppliances.value = it
            }
        }
    }

    fun updateUserRole(user: User, ordinal: Int) {
        viewModelScope.launch {
            usersRepository.updateUserField(user.userId, mapOf("role" to ordinal))
        }
    }


}
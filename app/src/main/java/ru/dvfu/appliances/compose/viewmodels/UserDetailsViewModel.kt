package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.UserRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.repository.entity.User

class UserDetailsViewModel(
    private val userRepository: UserRepository,
    private val repository: Repository,
) : ViewModel() {

    companion object {
        val defUser = User()
    }

    val currentUser: MutableStateFlow<User> = MutableStateFlow(defUser)

    val currentUserAppliances = MutableStateFlow<List<Appliance>?>(null)
    val currentSuperUserAppliances = MutableStateFlow<List<Appliance>?>(null)

    //val currentUser = userRepository.currentUserFromDB

    fun setUser(user: User) {
        if (currentUser.value == defUser) {
            currentUser.value = user
            updateUser()
            when(user.role) {
                Roles.USER.ordinal -> { getUserAppliances(user) }
                Roles.SUPERUSER.ordinal -> {
                    getUserAppliances(user);
                    getSuperUserAppliances(user) }
            }
        }
    }

    private fun updateUser() {
        viewModelScope.launch {
            userRepository.getUserWithId(currentUser.value.userId).collect {
                currentUser.value = it
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



}
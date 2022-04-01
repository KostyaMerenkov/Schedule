package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.repository.entity.User

class UserDetailsViewModel(
    private val detUser: User,
    private val usersRepository: UsersRepository,
    private val repository: AppliancesRepository,
    private val userDatastore: UserDatastore
) : ViewModel() {

    val currentUser: MutableStateFlow<User> = MutableStateFlow(User())
    val detailsUser: MutableStateFlow<User> = MutableStateFlow(User())

    init {
        getCurrentUser()
        setDetailsUser(detUser)
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            currentUser.value = userDatastore.getCurrentUser.first()
        }
    }


    val currentUserAppliances = MutableStateFlow<List<Appliance>?>(null)
    val currentSuperUserAppliances = MutableStateFlow<List<Appliance>?>(null)

    fun setDetailsUser(user: User) {
        detailsUser.value = user
        getUserAppliances(user);
        getSuperUserAppliances(user)
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
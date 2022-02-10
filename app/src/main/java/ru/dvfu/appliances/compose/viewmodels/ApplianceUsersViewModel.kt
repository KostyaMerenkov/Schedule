/*
package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User

class ApplianceUsersViewModel(private val repository: Repository) : ViewModel() {

    private val currentUsers = MutableStateFlow<List<User>>(listOf())
    private val currentSuperUsers = MutableStateFlow<List<User>>(listOf())

    fun loadAllUsers(updatedAppliance: Appliance) {
        viewModelScope.launch {
            repository.getApplianceUsers(updatedAppliance.userIds.toList()).collect { users ->
                currentUsers.value = users
            }
        }
    }

    fun loadAllSuperUsers(updatedAppliance: Appliance) {
        viewModelScope.launch {
            repository.getApplianceUsers(updatedAppliance.superuserIds.toList()).collect { users ->
                currentSuperUsers.value = users
            }
        }
    }

    fun deleteUser(userToDelete: User, from: Appliance) {
        viewModelScope.launch {
            repository.deleteUserFromAppliance(userToDelete, from)
        }
    }

    fun deleteSuperUser(superUserToDelete: User, from: Appliance) {
        viewModelScope.launch {
            //repository.deleteSuperUserFromAppliance(superUserToDelete, from)
        }
    }

}
*/

package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.use_cases.DeleteApplianceUseCase
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User

class ApplianceDetailsViewModel(
    private val usersRepository: UsersRepository,
    private val repository: AppliancesRepository,
    private val deleteApplianceUseCase: DeleteApplianceUseCase,
    private val userDatastore: UserDatastore,
) : ViewModel() {

    companion object {
        val defAppliance = Appliance()
    }

    var appliance: MutableStateFlow<Appliance> = MutableStateFlow(defAppliance)

    val currentUsers = MutableStateFlow<List<User>?>(null)
    val currentSuperUsers = MutableStateFlow<List<User>?>(null)
    val currentUser = userDatastore.getCurrentUser

    fun setAppliance(applianceFromArg: Appliance) {
        if (appliance.value == defAppliance) {
            loadAllUsers(applianceFromArg.userIds)
            loadAllSuperUsers(applianceFromArg.superuserIds)
            appliance.value = applianceFromArg
            updateAppliance()
        }
    }

    private fun updateAppliance() {
        viewModelScope.launch {
            repository.getAppliance(appliance.value.id).collect {
                it.fold(
                    onSuccess = { updatedAppliance ->
                        if (updatedAppliance.userIds != currentUsers.value?.map { it.userId })
                            loadAllUsers(updatedAppliance.userIds)
                        if (updatedAppliance.superuserIds != currentSuperUsers.value?.map { it.userId })
                            loadAllSuperUsers(updatedAppliance.superuserIds)
                        appliance.value = updatedAppliance
                    },
                    onFailure = {
                        // TODO:
                    }
                )
            }
        }
    }

    fun deleteAppliance() {
        viewModelScope.launch {
            appliance.value.let {
                deleteApplianceUseCase(it.id).single().fold(
                    onSuccess = {
                        SnackbarManager.showMessage(R.string.appliance_deleted)
                    }, onFailure = {
                        SnackbarManager.showMessage(R.string.appliance_delete_failed)
                    }
                )
            }
        }
    }

    fun loadAllUsers(ids: List<String>) {
        viewModelScope.launch {
            repository.getApplianceUsers(ids).collect { users ->
                currentUsers.value = users
            }
        }
    }

    fun loadAllSuperUsers(ids: List<String>) {
        viewModelScope.launch {
            repository.getApplianceUsers(ids).collect { users ->
                currentSuperUsers.value = users
            }
        }
    }

    fun deleteUser(userToDelete: User, from: Appliance) {
        viewModelScope.launch {
            repository.deleteUserFromAppliance(userToDelete.userId, from)
        }
    }

    fun deleteSuperUser(superUserToDelete: User, from: Appliance) {
        viewModelScope.launch {
            repository.deleteSuperUserFromAppliance(superUserToDelete.userId, from)
        }
    }

}
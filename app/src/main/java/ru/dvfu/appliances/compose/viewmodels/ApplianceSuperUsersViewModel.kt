package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User

class ApplianceSuperUsersViewModel(private val repository: Repository) : ViewModel() {


    val currentContent = MutableStateFlow<List<User>>(listOf())

    fun loadAllSuperUsers(appliance: Appliance) {
        viewModelScope.launch {
            if (appliance.superuserIds.isNotEmpty()) {
                repository.getApplianceUsers(appliance.superuserIds).collect { users ->
                    currentContent.value = users as List<User>
                }
            }
        }
    }

}
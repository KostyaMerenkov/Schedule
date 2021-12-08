package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.UserRepository
import ru.dvfu.appliances.model.repository.entity.Appliance

class ApplianceViewModel(
    private val userRepository: UserRepository,
    private val repository: Repository,
) : ViewModel() {

    var appliance: MutableStateFlow<Appliance> = MutableStateFlow(Appliance())

    val currentUser = userRepository.currentUserFromDB

    fun setAppliance(appliance: Appliance) {
        this.appliance = MutableStateFlow(appliance)
        //updateAppliance()
    }

    private fun updateAppliance() {
        viewModelScope.launch {
            appliance.value?.let {
                repository.getAppliance(it).collect { updatedAppliance ->
                    appliance.value = updatedAppliance
                }
            }
        }
    }

    fun deleteAppliance() {
        viewModelScope.launch {
            appliance.value?.let {
                repository.deleteAppliance(it)
            }
        }
    }

}
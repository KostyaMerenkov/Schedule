package ru.dvfu.appliances.model.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.UserRepository
import ru.dvfu.appliances.model.repository.entity.Appliance

class ApplianceViewModel(
    private val userRepository: UserRepository,
    private val repository: Repository,
) : ViewModel() {

    val appliance: MutableState<Appliance?> = mutableStateOf(null)

    /*fun getCatchesByMarkerId(markerId: String): Flow<List<UserCatch>> {
        return viewModelScope.run {
            repository.getCatchesByMarkerId(markerId)
        }
    }*/

    fun deleteAppliance() {
        viewModelScope.launch {
            appliance.value?.let {
                repository.deleteAppliance(it)
            }

        }
    }

}
package ru.dvfu.appliances.model.viewmodels

import androidx.lifecycle.ViewModel
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.UserRepository

class UserDetailsViewModel(
    private val userRepository: UserRepository,
    private val repository: Repository,
) : ViewModel() {

    //val appliance: MutableState<Appliance?> = mutableStateOf(null)

    /*fun getCatchesByMarkerId(markerId: String): Flow<List<UserCatch>> {
        return viewModelScope.run {
            repository.getCatchesByMarkerId(markerId)
        }
    }*/

    /*fun deleteAppliance() {
        viewModelScope.launch {
            appliance.value?.let {
                repository.deleteAppliance(it)
            }

        }
    }
*/
}
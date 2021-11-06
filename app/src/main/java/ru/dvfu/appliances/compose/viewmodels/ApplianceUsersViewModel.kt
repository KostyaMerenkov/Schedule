package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.entity.User

class ApplianceUsersViewModel(private val repository: Repository) {

    val currentContent = MutableStateFlow<List<User>>(listOf())

    /*private fun loadAllUsers() {
        viewModelScope.launch {
            repository.getAllUserMarkersList().collect { userPlaces ->
                currentContent.value = userPlaces as List<UserMapMarker>
            }
        }
    }*/
}

package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.ViewState

class AppliancesViewModel(
    private val repository: AppliancesRepository,
    private val usersRepository: UsersRepository,
    private val userDatastore: UserDatastore,
) : ViewModel() {

    private val _appliancesState = MutableStateFlow<ViewState<List<Appliance>>>(ViewState.Loading())
    val appliancesState = _appliancesState.asStateFlow()

    init {
        loadAppliances()
    }

    val user = userDatastore.getCurrentUser

    private fun loadAppliances() {
        _appliancesState.value = ViewState.Loading()
        viewModelScope.launch {
            repository.getAppliances().collect { appliances ->
                _appliancesState.value = ViewState.Success(appliances)
            }
        }
    }

}
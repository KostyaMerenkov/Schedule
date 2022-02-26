package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.ui.ViewState

class AppliancesViewModel(
    private val repository: AppliancesRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    val appliancesList = MutableStateFlow(listOf<Appliance>())

    val isRefreshing = mutableStateOf<Boolean>(false)

    init {
        loadAppliances()
    }

    private val _uiState = MutableStateFlow<ViewState<List<Appliance>>>(ViewState.Loading())
    val uiState: StateFlow<ViewState<List<Appliance>>>
        get() = _uiState

    fun refresh() = loadAppliances()

    val user = usersRepository.currentUserFromDB

    private fun loadAppliances() {
        isRefreshing.value = true
        viewModelScope.launch {
            repository.getAppliances().collect { appliances ->
                delay(1000)
                _uiState.value = ViewState.Success(appliances)
                isRefreshing.value = false
            }
        }
    }

}
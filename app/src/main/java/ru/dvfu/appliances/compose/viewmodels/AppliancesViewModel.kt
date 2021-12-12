package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.UserRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.ui.BaseViewState

class AppliancesViewModel(private val repository: Repository,
                          private val userRepository: UserRepository) : ViewModel() {

    val appliancesList = MutableStateFlow(listOf<Appliance>())

    val isRefreshing = mutableStateOf<Boolean>(false)

    init {
        loadAppliances()
    }

    private val _uiState = MutableStateFlow<BaseViewState>(BaseViewState.Success(null))
    val uiState: StateFlow<BaseViewState>
        get() = _uiState

    fun refresh() = loadAppliances()

    val user = userRepository.currentUserFromDB

    private fun loadAppliances() {
        if (appliancesList.value.isEmpty()) {


        isRefreshing.value = true
        viewModelScope.launch {
            repository.getAppliances().collect { appliances ->
                delay(1000)
                appliancesList.value = appliances as List<Appliance>
                isRefreshing.value = false
            }

        }        }
    }

}
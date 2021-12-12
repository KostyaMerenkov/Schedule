package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.BaseViewState

class UsersViewModel(
    private val repository: Repository
) : ViewModel() {

    val usersList = MutableStateFlow(listOf<User>())
    val isRefreshing = mutableStateOf<Boolean>(false)

    init {
        loadUsers()
    }

    private val _uiState = MutableStateFlow<BaseViewState>(BaseViewState.Success(null))
    val uiState: StateFlow<BaseViewState>
        get() = _uiState

    fun refresh() = loadUsers()

    private fun loadUsers() {
        if (usersList.value.isEmpty()) {
            isRefreshing.value = true
            viewModelScope.launch {
                repository.getUsers().collect { users ->
                    delay(1000)
                    usersList.value = users as List<User>
                    isRefreshing.value = false
                }
            }
        }
    }

}
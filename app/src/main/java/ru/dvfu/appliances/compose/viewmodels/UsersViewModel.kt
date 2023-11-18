package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.ViewState

class UsersViewModel(
    private val repository: UsersRepository
) : ViewModel() {

    private val _usersState = MutableStateFlow<ViewState<List<User>>>(ViewState.Loading)
    val userState = _usersState.asStateFlow()

    init {
        loadUsers()
    }

    private val _uiState = MutableStateFlow<BaseViewState>(BaseViewState.Success(null))
    val uiState: StateFlow<BaseViewState>
        get() = _uiState

    fun refresh() = loadUsers()

    private fun loadUsers() {
        _usersState.value = ViewState.Loading
        viewModelScope.launch {
            repository.getUsers().collect { users ->
                _usersState.value = ViewState.Success(users)
            }
        }
    }
}

package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.BaseViewState

class ProfileViewModel(
    private val userDatastore: UserDatastore,
    private val usersRepository: UsersRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BaseViewState>(BaseViewState.Success(null))
    val uiState: StateFlow<BaseViewState>
        get() = _uiState

    private val _currentUser = MutableStateFlow<User>(User())
    val currentUser = _currentUser.asStateFlow()

    init {
        getCurrentUser()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            userDatastore.getCurrentUser.collect {
                _currentUser.value = it
            }
        }
    }

    suspend fun logoutCurrentUser() = usersRepository.logoutCurrentUser()


}
package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.dvfu.appliances.model.repository.UserRepository
import ru.dvfu.appliances.ui.BaseViewState

class ProfileViewModel(
    private val userRepository: UserRepository,
    //private val repository: UserContentRepository
) : ViewModel() {

    /*init {
        getUserCatches()
    }*/

    private val _uiState = MutableStateFlow<BaseViewState>(BaseViewState.Success(null))
    val uiState: StateFlow<BaseViewState>
        get() = _uiState

    fun getCurrentUser() = userRepository.currentUser

    suspend fun logoutCurrentUser() = userRepository.logoutCurrentUser()


}
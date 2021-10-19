package ru.dvfu.appliances.model.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.UserRepository
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.Progress

class LoginViewModel(private val userRepository: UserRepository, private val repository: UserRepository) : ViewModel() {

    private val mutableStateFlow: MutableStateFlow<BaseViewState> =
        MutableStateFlow(BaseViewState.Success(null))

    fun subscribe(): StateFlow<BaseViewState> = mutableStateFlow

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.currentUser
                .catch { error -> handleError(error) }
                .collectLatest { user -> user?.let { onSuccess(it) } }
        }
    }

    private fun onSuccess(user: User) {
        viewModelScope.launch {
            repository.addNewUser(user).collect { progress ->
                when (progress) {
                    is Progress.Complete -> {
                        mutableStateFlow.value = BaseViewState.Success(user)
                    }
                    is Progress.Loading -> {
                        mutableStateFlow.value = BaseViewState.Loading(null)
                    }
                    is Progress.Error -> {
                        mutableStateFlow.value =
                            BaseViewState.Error(progress.error)
                    }
                }
            }
            //mutableStateFlow.value = BaseViewState.Success(user)
        }
    }

    private fun handleError(error: Throwable) {
        mutableStateFlow.value = BaseViewState.Error(error)
    }

}
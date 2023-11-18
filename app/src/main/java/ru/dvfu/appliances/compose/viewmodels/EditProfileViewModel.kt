package ru.dvfu.appliances.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.ViewState

class EditProfileViewModel(
    private val userDatastore: UserDatastore,
    private val userRepository: UsersRepository,
) : ViewModel() {

    private val _bdUser = MutableStateFlow(User())

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    private val _isChanged = MutableStateFlow(false)
    val isChanged = _isChanged.asStateFlow()

    init {
        loadCurrentUser()
        setChangedListener()
    }

    private val _uiState = MutableStateFlow<ViewState<Unit>?>(null)
    val uiState = _uiState.asStateFlow()

    fun resetChanges() {
        loadCurrentUser()
    }

    fun onNameChange(name: String) {
        _currentUser.value = _currentUser.value.copy(userName = name)
    }

    fun birthdaySelected(birthday: Long) {
        _currentUser.value = _currentUser.value.copy(birthday = birthday)
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = userDatastore.getCurrentUser.first()
            _bdUser.value = user
            _currentUser.value = user
        }
    }

    private fun setChangedListener() {
        viewModelScope.launch {
            currentUser.collect {
                /*_isChanged.value = it != _bdUser.value*/
                _isChanged.value =
                    (it.userName != _bdUser.value.userName
                            || it.email != _bdUser.value.email
                            || it.birthday != _bdUser.value.birthday)
            }
        }
    }

    fun updateProfile() {
        _uiState.value = ViewState.Loading
        viewModelScope.launch {
            userRepository.setNewProfileData(
                _currentUser.value.userId,
                mapOf(
                    Pair("userName", currentUser.value.userName),
                    Pair("birthday", currentUser.value.birthday)
                )
            ).fold(
                onSuccess = {
                    _uiState.value = ViewState.Success(Unit)
                }, onFailure = {
                    _uiState.value = ViewState.Error(it)
                }
            )
        }
    }
}
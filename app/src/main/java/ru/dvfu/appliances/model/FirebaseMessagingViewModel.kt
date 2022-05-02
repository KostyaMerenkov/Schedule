package ru.dvfu.appliances.model

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.ViewState
import java.time.*

class FirebaseMessagingViewModel(
    private val usersRepository: UsersRepository,
) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @OptIn(DelicateCoroutinesApi::class)
    fun onNewToken(newToken: String) {
        scope.launch {
            usersRepository.setNewMessagingToken(newToken)
            cancel()
        }
    }

    fun onDestroy() {
        job.cancel()
    }

}
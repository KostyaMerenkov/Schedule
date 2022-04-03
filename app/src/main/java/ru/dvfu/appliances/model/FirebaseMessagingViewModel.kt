package ru.dvfu.appliances.model

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.event_calendar.CalendarEvent
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
    private val getUserUseCase: GetUserUseCase,
    private val usersRepository: UsersRepository,
    private val userDatastore: UserDatastore,
    private val appliancesRepository: AppliancesRepository,
) {


    @OptIn(DelicateCoroutinesApi::class)
    fun onNewToken(newToken: String) {
        GlobalScope.launch {
            usersRepository.setNewMessagingToken(newToken)
        }
    }


}
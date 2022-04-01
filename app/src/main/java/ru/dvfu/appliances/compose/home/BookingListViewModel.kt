package ru.dvfu.appliances.compose.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.event_calendar.CalendarEvent
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.model.repository.BookingRepository
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.*
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.ui.ViewState
import java.time.*

class BookingListViewModel(
    private val bookingRepository: BookingRepository,
    private val offlineRepository: OfflineRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getApplianceUseCase: GetApplianceUseCase,
) : ViewModel() {

    val selectedEvent = mutableStateOf<CalendarEvent?>(null)

    private val _reposBookingList = MutableStateFlow<List<Booking>>(listOf())

    private val _uiState = MutableStateFlow<ViewState<MutableList<UiBooking>>>(ViewState.Loading())
    val uiState: StateFlow<ViewState<List<UiBooking>>> = _uiState.asStateFlow()

    private val _bookingList = MutableStateFlow<MutableList<UiBooking>>(mutableListOf())
    val bookingList: StateFlow<List<UiBooking>> = _bookingList.asStateFlow()

    private val appliances = MutableStateFlow<List<Appliance>>(listOf())
    private val users = MutableStateFlow<List<User>>(listOf())

    init {
        //getAppliances()
        getBookings()
    }

    val mutableStateFlow: MutableStateFlow<ViewState<User>> =
        MutableStateFlow(ViewState.Loading(null))

    private fun getBookings() {
        viewModelScope.launch {
            _uiState.value = ViewState.Loading()
            bookingRepository.getAllBooking().collect { result ->
                result.fold(
                    onSuccess = {
                        _reposBookingList.value = it

                        _bookingList.value = it.map { currentBooking ->
                            UiBooking(
                                id = currentBooking.id,
                                timeStart = currentBooking.timeStart.toLocalDateTime(),
                                timeEnd = currentBooking.timeEnd.toLocalDateTime(),
                                commentary = currentBooking.commentary,
                                user = getUserUseCase(currentBooking.userId).first().getOrThrow(),
                                appliance = getApplianceUseCase(currentBooking.applianceId).first().getOrDefault(null),
                                managedUser = if (currentBooking.managedById.isBlank()) null else
                                    getUserUseCase(currentBooking.managedById).first().getOrDefault(null)
                            )
                        }.toMutableList()
                        _uiState.value = ViewState.Success(_bookingList.value)
                    },
                    onFailure = {
                        _uiState.value = ViewState.Error(it)
                    }
                )
            }
        }
    }

    private suspend fun getAppliances() {
        offlineRepository.getAppliances().collect {
            appliances.value = it
        }
    }

    fun deleteBooking(idToDelete: String) {
        viewModelScope.launch {
            bookingRepository.deleteBooking(idToDelete).fold(
                onSuccess = {
                    val newBookingsList = _bookingList.value.filter { it.id != idToDelete }.toMutableList()
                    _bookingList.value = newBookingsList
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.event_delete_failed)
                }
            )
        }
    }

}
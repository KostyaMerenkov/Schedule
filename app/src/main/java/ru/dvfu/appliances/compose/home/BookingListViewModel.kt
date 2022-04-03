package ru.dvfu.appliances.compose.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.event_calendar.CalendarEvent
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.BookingRepository
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
    private val userDatastore: UserDatastore,
) : ViewModel() {

    val selectedEvent = mutableStateOf<CalendarEvent?>(null)

    private val _reposBookingList = MutableStateFlow<List<Booking>>(listOf())

    private val _currentUser = MutableStateFlow<User>(User())
    val currentUser = _currentUser.asStateFlow()

    private val _managingUiState = MutableStateFlow<UiState?>(null)
    val managingUiState = _managingUiState.asStateFlow()

    private val _uiState = MutableStateFlow<ViewState<MutableList<UiBooking>>>(ViewState.Loading())
    val uiState: StateFlow<ViewState<List<UiBooking>>> = _uiState.asStateFlow()

    private val _bookingList = MutableStateFlow<MutableList<UiBooking>>(mutableListOf())
    val bookingList: StateFlow<List<UiBooking>> = _bookingList.asStateFlow()

    private val appliances = MutableStateFlow<List<Appliance>>(listOf())
    private val users = MutableStateFlow<List<User>>(listOf())

    init {
        getCurrentUser()
        getBookings()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = userDatastore.getCurrentUser.first()
        }
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
                                appliance = getApplianceUseCase(currentBooking.applianceId).first()
                                    .getOrThrow(),
                                managedUser = if (currentBooking.managedById.isBlank()) null else
                                    getUserUseCase(currentBooking.managedById).first()
                                        .getOrDefault(null),
                                managedTime = currentBooking.managedTime,
                                status = currentBooking.status,
                                managerCommentary = currentBooking.managerCommentary
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

    fun declineBook(book: UiBooking) {
        viewModelScope.launch {
            if (couldManageBooks(currentUser.value, book)) {
                _managingUiState.value = UiState.InProgress
                val managedTime = LocalDateTime.now().toMillis
                bookingRepository.declineBooking(
                    bookId = book.id,
                    managedById = currentUser.value.userId,
                    managerCommentary = "",
                    managedTime = managedTime
                ).fold(
                    onSuccess = {
                        newBookStatus(
                            book,
                            status = BookingStatus.DECLINED,
                            managedById = currentUser.value.userId,
                            managerCommentary = "",
                            managedTime = managedTime
                        )
                        _managingUiState.value = UiState.Success
                        SnackbarManager.showMessage(R.string.book_declined)
                    },
                    onFailure = {
                        SnackbarManager.showMessage(R.string.book_decline_failed)
                        _managingUiState.value = UiState.Error

                    }
                )
            } else SnackbarManager.showMessage(R.string.no_permissions)
        }
    }

    fun approveBook(book: UiBooking) {
        viewModelScope.launch {
            if (couldManageBooks(currentUser.value, book)) {
                _managingUiState.value = UiState.InProgress
                val managedTime = LocalDateTime.now().toMillis
                bookingRepository.approveBooking(
                    bookId = book.id,
                    managedById = currentUser.value.userId,
                    managerCommentary = "",
                    managedTime = managedTime
                ).fold(
                    onSuccess = {
                        newBookStatus(
                            book,
                            status = BookingStatus.APPROVED,
                            managedById = currentUser.value.userId,
                            managerCommentary = "",
                            managedTime = managedTime
                        )
                        SnackbarManager.showMessage(R.string.book_approved)
                        _managingUiState.value = UiState.Success
                    },
                    onFailure = {
                        SnackbarManager.showMessage(R.string.book_approve_failed)
                        _managingUiState.value = UiState.Error
                    }
                )
            } else SnackbarManager.showMessage(R.string.no_permissions)
        }
    }

    private suspend fun newBookStatus(
        book: UiBooking,
        status: BookingStatus,
        managedById: String,
        managerCommentary: String,
        managedTime: Long
    ) {
        _bookingList.value = _bookingList.value.apply {
            find { it.id == book.id }?.let {
                val itemIndex = indexOf(it)
                this.remove(it)
                this.add(
                    index = itemIndex,
                    element = it.copy(
                        status = status,
                        managedUser = getUserUseCase(managedById).first().getOrDefault(User()),
                        managerCommentary = managerCommentary,
                        managedTime = managedTime
                    )
                )
            }
        }
    }

    private fun couldManageBooks(user: User, book: UiBooking): Boolean {
        return user.isAdmin() || book.appliance.superuserIds.contains(user.userId)
    }

    fun deleteBooking(idToDelete: String) {
        viewModelScope.launch {
            bookingRepository.deleteBooking(idToDelete).fold(
                onSuccess = {
                    val newBookingsList =
                        _bookingList.value.filter { it.id != idToDelete }.toMutableList()
                    _bookingList.value = newBookingsList
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.event_delete_failed)
                }
            )
        }
    }


}
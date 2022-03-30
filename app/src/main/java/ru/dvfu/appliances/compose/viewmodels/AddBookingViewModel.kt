package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.dvfu.appliances.model.datastore.UserDatastore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.BookingRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Booking
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.ui.ViewState
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class AddBookingViewModel(
    private val usersRepository: UsersRepository,
    private val appliancesRepository: AppliancesRepository,
    private val bookingRepository: BookingRepository,
    private val offlineRepository: OfflineRepository,
    private val userDatastore: UserDatastore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _appliancesState = MutableStateFlow<ViewState<List<Appliance>>>(ViewState.Loading())
    val appliancesState = _appliancesState.asStateFlow()

    private val _selectedAppliance = MutableStateFlow<Appliance?>(null)
    val selectedAppliance = _selectedAppliance.asStateFlow()

    private val _booking = MutableStateFlow(
        Booking().copy(
            timeStart = Calendar.getInstance().timeInMillis,
            timeEnd = Calendar.getInstance().apply { add(Calendar.HOUR, 1) }.timeInMillis
        )
    )
    val booking = _booking.asStateFlow()

    val date = mutableStateOf(0L)

    val isDurationError: MutableStateFlow<Boolean>
        get() = MutableStateFlow(
            TimeUnit.MILLISECONDS.toMinutes(booking.value.timeEnd - booking.value.timeStart) < 0
                    || Instant.ofEpochMilli(booking.value.timeStart).atZone(ZoneId.systemDefault())
                .toLocalDate() != Instant.ofEpochMilli(booking.value.timeEnd)
                .atZone(ZoneId.systemDefault()).toLocalDate()
        )
    val duration: MutableStateFlow<String>
        get() {
            /*       LocalDateTime
                   ChronoUnit.MINUTES.between(calendarEvent.start, calendarEvent.end)*/
            val mills = booking.value.timeEnd - booking.value.timeStart
            val period = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(mills),
                TimeUnit.MILLISECONDS.toMinutes(mills) % TimeUnit.HOURS.toMinutes(1)
            )
            return MutableStateFlow(period)
        }

    val isError: MutableStateFlow<Boolean>
        get() = MutableStateFlow<Boolean>(
            isDurationError.value || booking.value.applianceId.isEmpty()
        )

    init {
        loadAppliancesOffline()
    }

    private fun loadAppliancesOffline() {
        viewModelScope.launch {
            offlineRepository.getAppliances().collect { appliances ->
                _appliancesState.value = ViewState.Success(appliances)
            }
        }
    }

    fun createBooking() {
        val booking = booking.value
        viewModelScope.launch {
            if (isError.value) {
                showError()
            } else {
                val userId = userDatastore.getCurrentUser.first().userId
                val bookingToUpload = booking.copy(
                    id = UUID.randomUUID().toString(),
                    userId = userId
                )
                bookingRepository.createBooking(bookingToUpload)
                delay(500)
                _uiState.value = UiState.Success

            }
        }
    }

    private fun showError() {
        when {
            TimeUnit.MILLISECONDS.toMinutes(booking.value.timeEnd - booking.value.timeStart) < TimeUnit.MILLISECONDS.toMinutes(
                10
            ) -> {
                SnackbarManager.showMessage(R.string.duration_error)
            }
            booking.value.applianceId.isEmpty() -> {
                SnackbarManager.showMessage(R.string.appliance_not_chosen)
            }
            else -> SnackbarManager.showMessage(R.string.error_occured)
        }
    }

    fun onApplianceSelected(appliance: Appliance) {
        _booking.value = booking.value.copy(applianceId = appliance.id)
        _selectedAppliance.value = appliance
    }

    fun onCommentarySet(commentary: String) {
        _booking.value = booking.value.copy(commentary = commentary)
    }

    fun onDateSet(date: Long) {
        val calendarToSet = Calendar.getInstance().apply { timeInMillis = date }
        _booking.value = booking.value.copy(timeStart = Calendar.getInstance().apply {
            timeInMillis = _booking.value.timeStart
            set(Calendar.YEAR, calendarToSet.get(Calendar.YEAR))
            set(Calendar.MONTH, calendarToSet.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, calendarToSet.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis)

        _booking.value = booking.value.copy(timeEnd = Calendar.getInstance().apply {
            timeInMillis = _booking.value.timeEnd
            set(Calendar.YEAR, calendarToSet.get(Calendar.YEAR))
            set(Calendar.MONTH, calendarToSet.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, calendarToSet.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis)
    }

    fun onTimeStartSet(time: Long) {
        val calendarToSet = Calendar.getInstance().apply { timeInMillis = time }
        _booking.value = booking.value.copy(timeStart = Calendar.getInstance().apply {
            timeInMillis = _booking.value.timeStart
            set(Calendar.HOUR, calendarToSet.get(Calendar.HOUR))
            set(Calendar.MINUTE, calendarToSet.get(Calendar.MINUTE))
        }.timeInMillis)
    }

    fun onTimeEndSet(time: Long) {
        val calendarToSet = Calendar.getInstance().apply { timeInMillis = time }
        _booking.value = booking.value.copy(timeEnd = Calendar.getInstance().apply {
            timeInMillis = _booking.value.timeEnd
            set(Calendar.HOUR, calendarToSet.get(Calendar.HOUR))
            set(Calendar.MINUTE, calendarToSet.get(Calendar.MINUTE))
        }.timeInMillis)
    }

}
package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import ru.dvfu.appliances.model.datastore.UserDatastore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.use_cases.GetAppliancesUseCase
import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.BookingRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Booking
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.ui.ViewState
import java.time.*
import java.util.*
import java.util.concurrent.TimeUnit

class AddBookingViewModel(
    private val bookingRepository: BookingRepository,
    private val getAppliancesUseCase: GetAppliancesUseCase,
    private val userDatastore: UserDatastore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _appliancesState = MutableStateFlow<ViewState<List<Appliance>>>(ViewState.Loading())
    val appliancesState = _appliancesState.asStateFlow()

    private val _selectedAppliance = MutableStateFlow<Appliance?>(null)
    val selectedAppliance = _selectedAppliance.asStateFlow()

    val date = MutableStateFlow(LocalDate.now())
    val timeStart = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val timeEnd = MutableStateFlow<LocalDateTime>(LocalDateTime.now())
    val commentary = MutableStateFlow("")

    val isDurationError: MutableStateFlow<Boolean>
        get() = MutableStateFlow(timeEnd.value.isBefore(timeStart.value))
    val duration: MutableStateFlow<String>
        get() {
            val dur = Duration.between(timeStart.value, timeEnd.value)

            val period = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                dur.toHours(),
                dur.minusHours(dur.toHours()).toMinutes(),
            )
            return MutableStateFlow(period)
        }

    private val isError: MutableStateFlow<Boolean>
        get() = MutableStateFlow(isDurationError.value || selectedAppliance.value == null)


    init {
        loadAppliancesOffline()
    }

    private fun loadAppliancesOffline() {
        viewModelScope.launch {
            getAppliancesUseCase.invoke().collect { appliances ->
                _appliancesState.value = ViewState.Success(appliances)
            }
        }
    }

    fun createBooking() {
        viewModelScope.launch(Dispatchers.Default) {
            if (isError.value) {
                showError()
                this.cancel()
            }
            //val user = userDatastore.getCurrentUser.first<User>()
            _uiState.value = UiState.InProgress
            selectedAppliance.value?.let {
                val bookingToUpload = Booking(
                    id = UUID.randomUUID().toString(),
                    userId = userDatastore.getCurrentUser.first<User>().userId,
                    timeStart = timeStart.value.toMillis,
                    timeEnd = timeEnd.value.toMillis,
                    commentary = commentary.value,
                    applianceId = it.id,
                )
                bookingRepository.createBooking(bookingToUpload)
                delay(500)
                _uiState.value = UiState.Success
            }
        }
    }

    private fun showError() {
        when {
            Duration.between(
                timeEnd.value,
                timeStart.value
            ) < Duration.ofMinutes(10) /*TimeUnit.MILLISECONDS.toMinutes(10)*/ -> {
                SnackbarManager.showMessage(R.string.duration_error)
            }
            selectedAppliance.value == null -> {
                SnackbarManager.showMessage(R.string.appliance_not_chosen)
            }
            else -> SnackbarManager.showMessage(R.string.error_occured)
        }
    }

    fun onApplianceSelected(appliance: Appliance) {
        _selectedAppliance.value = appliance.takeIf { it != _selectedAppliance.value }
    }

    fun onCommentarySet(commentary: String) {
        this.commentary.value = commentary
    }

    fun onDateSet(date: LocalDate) {
        this.date.value = LocalDate.of(date.year, date.month, date.dayOfMonth)
        timeStart.value = date.atTime(timeStart.value.hour, timeStart.value.minute)
        timeEnd.value = date.atTime(timeEnd.value.hour, timeEnd.value.minute)
    }

    fun onTimeStartSet(time: LocalTime) {
        timeStart.value = date.value.atTime(time.hour, time.minute)
    }

    fun onTimeEndSet(time: LocalTime) {
        timeEnd.value = date.value.atTime(time.hour, time.minute)
    }

}
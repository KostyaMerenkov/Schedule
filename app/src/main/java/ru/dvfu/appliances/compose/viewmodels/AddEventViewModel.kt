package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.ViewState
import java.util.*
import java.util.concurrent.TimeUnit

class AddEventViewModel(
    private val repository: Repository,
) : ViewModel() {

    val isRefreshing = mutableStateOf<Boolean>(false)

    private val _uiState = MutableStateFlow<BaseViewState>(BaseViewState.Success(null))
    val uiState: StateFlow<BaseViewState>
        get() = _uiState

    private val _appliancesState = MutableStateFlow<ViewState<List<Appliance>>>(ViewState.Loading())
    val appliancesState: StateFlow<ViewState<List<Appliance>>>
        get() = _appliancesState

    val date = mutableStateOf(0L)
    val timeStart = mutableStateOf(0L)
    val timeEnd = mutableStateOf(0L)

    val duration: MutableStateFlow<String>
        get() {
     /*       LocalDateTime
            ChronoUnit.MINUTES.between(event.start, event.end)*/
            val mills = timeEnd.value - timeStart.value
            val period = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(mills),
                TimeUnit.MILLISECONDS.toMinutes(mills) % TimeUnit.HOURS.toMinutes(1)
            )
            return MutableStateFlow(period)
        }

    init {
        loadAppliances()
    }

    private fun loadAppliances() {
        isRefreshing.value = true
        viewModelScope.launch {
            repository.getAppliances().collect { appliances ->
                delay(1000)
                _appliancesState.value = ViewState.Success(appliances)
                isRefreshing.value = false
            }
        }
    }

    fun getDuration() {
        val start = timeStart.value
        val end = timeEnd.value


        if (start != 0L && end != 0L) {
            val mills = end - start
            val period = String.format(
                Locale.getDefault(),
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(mills),
                TimeUnit.MILLISECONDS.toMinutes(mills) % TimeUnit.HOURS.toMinutes(1)
            )
            duration.value = period
        } else duration.value = ""
    }

}
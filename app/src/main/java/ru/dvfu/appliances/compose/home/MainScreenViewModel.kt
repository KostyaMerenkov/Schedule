package ru.dvfu.appliances.compose.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.boguszpawlowski.composecalendar.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.dvfu.appliances.compose.event_calendar.Event
import ru.dvfu.appliances.model.repository.EventsRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MainScreenViewModel(
    val eventsRepository: EventsRepository
) : ViewModel() {

    var events = MutableStateFlow<List<Event>>(listOf())

    init {
        getEvents()
    }

    private fun getEvents() {
        viewModelScope.launch {
            eventsRepository.getAllEventsFromDate(java.util.Calendar.getInstance().apply {
                add(java.util.Calendar.DATE, -1)
            }.timeInMillis).collect { result ->
                events.value = result.map {
                    Event(
                        name = it.applianceId,
                        start = Instant.ofEpochMilli(it.timeStart)
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        end = Instant.ofEpochMilli(it.timeEnd)
                            .atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        color = Color(0xFFF4BFDB),
                        description = it.commentary
                    )
                }

            }
        }
    }


}
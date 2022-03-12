package ru.dvfu.appliances.compose.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.dvfu.appliances.compose.event_calendar.Event
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.ui.Progress
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MainScreenViewModel(
    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository
) : ViewModel() {

    val selectedEvent = mutableStateOf(Event())
    private val _events = MutableStateFlow<MutableList<Event>>(mutableListOf())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val appliances = MutableStateFlow<List<Appliance>>(listOf())

    init {
        getAppliances()
        getEvents()
    }

    private fun getAppliances() {
        viewModelScope.launch {
            offlineRepository.getAppliances().collect {
                appliances.value = it
            }
        }
    }

    private fun getEvents() {
        viewModelScope.launch {
            eventsRepository.getAllEventsFromDate(java.util.Calendar.getInstance().apply {
                add(java.util.Calendar.DATE, -1)
            }.timeInMillis).collect { result ->
                    _events.value = result.map { currentEvent ->
                        val appliance = appliances.value.find { it.id == currentEvent.applianceId }
                        Event(
                            id = currentEvent.id,
                            name = appliance?.name ?: currentEvent.applianceId,
                            start = currentEvent.timeStart.toLocalDateTime(),
                            end = currentEvent.timeEnd.toLocalDateTime(),
                            color = Color(appliance?.color ?: 0),
                            description = currentEvent.commentary
                        )
                    }.toMutableList()
            }
        }
    }

    fun deleteEvent(eventToDelete: Event) {
        viewModelScope.launch {
            eventsRepository.deleteEvent(eventToDelete.id).collect {
                when (it) {
                    Progress.Complete -> {
                        val newEventsList = _events.value.filter { it.id != eventToDelete.id }.toMutableList()
                        _events.value = newEventsList
                    }
                    is Progress.Error -> {}
                    is Progress.Loading -> {}
                }
            }
        }
    }


}
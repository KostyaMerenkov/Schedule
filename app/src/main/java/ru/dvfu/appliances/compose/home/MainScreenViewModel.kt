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
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.ui.Progress
import java.time.LocalDate

class MainScreenViewModel(

    private val eventsRepository: EventsRepository,
    private val offlineRepository: OfflineRepository
) : ViewModel() {

    val selectedEvent = mutableStateOf<CalendarEvent?>(null)

    private val _reposEvents = MutableStateFlow<List<Event>>(listOf())

    private val _events = MutableStateFlow<MutableList<CalendarEvent>>(mutableListOf())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val appliances = MutableStateFlow<List<Appliance>>(listOf())

    init {
        //getAppliances()
        getEvents()
    }

    /*private fun getAppliances() {
        viewModelScope.launch {
            offlineRepository.getAppliances().collect {
                appliances.value = it
            }
        }
    }*/

    private fun getEvents() {
        viewModelScope.launch {
            eventsRepository.getAllEventsFromDate(LocalDate.now().minusDays(1L).toEpochDay())
                .collect { result ->
                    _reposEvents.value = result.toList()

                    _events.value = result.map { currentEvent ->
                        CalendarEvent(
                            id = currentEvent.id,
                            color = Color(currentEvent.color),
                            applianceName = currentEvent.applianceName,
                            applianceId = currentEvent.applianceId,
                            userId = currentEvent.userId,
                            superUserId = currentEvent.superUserId,
                            start = currentEvent.timeStart.toLocalDateTime(),
                            end = currentEvent.timeEnd.toLocalDateTime(),
                            description = currentEvent.commentary
                        )
                    }.toMutableList()
                }
        }
    }

    private suspend fun getAppliances() {
        offlineRepository.getAppliances().collect {
            appliances.value = it
        }
    }

    fun deleteEvent(eventToDelete: CalendarEvent) {
        viewModelScope.launch {
            eventsRepository.deleteEvent(eventToDelete.id).single().fold(
                onSuccess = {
                    val newEventsList =
                        _events.value.filter { it.id != eventToDelete.id }.toMutableList()
                    _events.value = newEventsList
                },
                onFailure = {
                    SnackbarManager.showMessage(R.string.event_delete_failed)
                }
            )
        }
    }

    fun getRepoEvent(calendarEvent: CalendarEvent): Event? {
        return _reposEvents.value.find { it.id == calendarEvent.id }
    }


}
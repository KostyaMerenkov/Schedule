package ru.dvfu.appliances.compose.calendars

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.calendars.event_calendar.Schedule
import ru.dvfu.appliances.compose.home.HomeTopBar
import ru.dvfu.appliances.compose.home.SelectedDate
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.viewmodels.WeekCalendarViewModel
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.isAnonymousOrGuest
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*

@Composable
fun EventCalendar(
    viewModel: WeekCalendarViewModel,
    navController: NavController,
    onEventLongClick: (CalendarEvent) -> Unit,
    horizontalScrollState: ScrollState,
    verticalScrollState: ScrollState,
) {
    val currentDate = remember { LocalDate.now() }
    val minDate = remember { currentDate.with(WeekFields.of(Locale("ru-RU")).dayOfWeek(), 1L) }
    val maxDate = remember { currentDate.with(WeekFields.of(Locale("ru-RU")).dayOfWeek(), 7L) }

    SideEffect {
        viewModel.getWeekEvents(minDate, maxDate)
    }

    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.weekEvents.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            HomeTopBar(uiState = uiState, onBookingListOpen = {
                navController.navigate(
                    MainDestinations.BOOKING_LIST,
                    Arguments.DATE to SelectedDate()
                )
            }, onCalendarSelected = viewModel::setCalendarType)
        },
        floatingActionButton = {
            if (!currentUser.isAnonymousOrGuest) {
                FloatingActionButton(
                    onClick = { navController.navigate(MainDestinations.ADD_EVENT) })
                { Icon(Icons.Default.Add, "") }
            }
        },
    ) {
        Schedule(
            modifier = Modifier.padding(it),
            calendarEvents = events,
            minDate = minDate,
            maxDate = maxDate,
            onEventClick = {
                navController.navigate(
                    MainDestinations.EVENT_INFO,
                    Arguments.EVENT to it
                )
            },
            onEventLongClick = onEventLongClick,
            verticalScrollState = verticalScrollState,
            horizontalScrollState = horizontalScrollState
        )
    }

}
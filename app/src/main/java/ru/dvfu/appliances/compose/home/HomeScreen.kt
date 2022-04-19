package ru.dvfu.appliances.compose.home

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.himanshoe.kalendar.ui.KalendarType
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.calendars.CalendarType
import ru.dvfu.appliances.compose.calendars.MonthWeekCalendar
import ru.dvfu.appliances.compose.components.FabWithMenu
import ru.dvfu.appliances.compose.components.MultiFabState
import ru.dvfu.appliances.compose.event_calendar.EventTimeFormatter
import ru.dvfu.appliances.compose.event_calendar.Schedule
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.viewmodels.WeekCalendarViewModel
import ru.dvfu.appliances.compose.views.DefaultDialog
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import java.time.LocalDate

@Composable
fun HomeScreen(
    navController: NavController,
) {
    //val viewModell: MainScreenViewModel = getViewModel()
    val viewModel: WeekCalendarViewModel = getViewModel()
    val calendarType by viewModel.calendarType.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()

    var eventOptionDialogOpened by remember { mutableStateOf(false) }
    if (eventOptionDialogOpened) EventOptionDialog(
        calendarEvent = viewModel.selectedEvent.value,
        onDismiss = { eventOptionDialogOpened = false },
        onDelete = viewModel::deleteEvent
    )

    val fabState = remember { mutableStateOf(MultiFabState.COLLAPSED) }

    Scaffold(topBar = {
        HomeTopBar(onBookingListOpen = {
            navController.navigate(MainDestinations.BOOKING_LIST)
        }, onCalendarSelected = viewModel::setCalendarType)
    }, floatingActionButton = {
        if (!currentUser.isAnonymousOrGuest()) {
            FabWithMenu(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .zIndex(8f),
                fabState = fabState,
                currentUser = currentUser,
                onAddEventClick = {
                    navController.navigate(
                        MainDestinations.ADD_EVENT,
                        Arguments.DATE to SelectedDate(currentDate)
                    )
                },
                onAddBookingClick = {
                    navController.navigate(
                        MainDestinations.ADD_BOOKING,
                        Arguments.DATE to SelectedDate(currentDate)
                    )
                }
            )
        }
    }
    ) {
        AnimatedVisibility(
            fabState.value == MultiFabState.EXPANDED,
            modifier = Modifier
                .zIndex(4f)
                .fillMaxSize(), enter = fadeIn(), exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(0.6f))
                    .clickable(role = Role.Image) {
                        fabState.value = MultiFabState.COLLAPSED
                    })
        }

        when (calendarType) {
            CalendarType.MONTH -> {
                MonthWeekCalendar(
                    viewModel = viewModel,
                    calendarType = KalendarType.Firey(),
                    onEventClick = {
                        viewModel.getRepoEvent(it)?.let {
                            navController.navigate(
                                MainDestinations.EVENT_INFO,
                                Arguments.EVENT to it
                            )
                        }
                    }) {
                    viewModel.selectedEvent.value = it
                    eventOptionDialogOpened = true
                }
            }
            CalendarType.WEEK -> {
                MonthWeekCalendar(viewModel = viewModel, onEventClick = {
                    viewModel.getRepoEvent(it)?.let {
                        navController.navigate(
                            MainDestinations.EVENT_INFO, Arguments.EVENT to it
                        )
                    }
                }) {
                    viewModel.selectedEvent.value = it
                    eventOptionDialogOpened = true
                }
            }
            CalendarType.THREE_DAYS -> {
                EventCalendar(viewModel = viewModel,
                    onEventClick = {
                        viewModel.getRepoEvent(it)?.let {
                            navController.navigate(
                                MainDestinations.EVENT_INFO,
                                Arguments.EVENT to it
                            )
                        }
                    }, onEventLongClick = {
                        viewModel.selectedEvent.value = it
                        eventOptionDialogOpened = true
                    })
            }
        }

    }
}

@Parcelize
data class SelectedDate(val value: LocalDate = LocalDate.now()) : Parcelable

@Composable
fun EventCalendar(
    viewModel: WeekCalendarViewModel,
    onEventClick: (CalendarEvent) -> Unit,
    onEventLongClick: (CalendarEvent) -> Unit,
) {
    SideEffect {
        viewModel.getThreeDaysEvents()
    }
    val events by viewModel.threeDaysEvents.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Schedule(
        calendarEvents = events, minDate = LocalDate.now().minusDays(1),
        maxDate = LocalDate.now().plusDays(6),
        onEventClick = onEventClick,
        onEventLongClick = onEventLongClick,
        verticalScrollState = verticalScrollState,
        horizontalScrollState = horizontalScrollState
    )
}

@Composable
fun HomeTopBar(onBookingListOpen: () -> Unit, onCalendarSelected: (CalendarType) -> Unit) {
    ScheduleAppBar(
        title = stringResource(id = R.string.schedule),
        //backgroundColor = Color(0xFFFF5470),
        actions = {
            var dropdownExpanded by remember { mutableStateOf(false) }

            IconButton(onClick = onBookingListOpen) {
                Icon(Icons.Default.Book, Icons.Default.Book.name)
            }
            IconButton(onClick = { dropdownExpanded = true }) {
                Icon(Icons.Default.EditCalendar, Icons.Default.EditCalendar.name)
            }
            DropdownMenu(
                modifier = Modifier.width(150.dp),
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }) {
                CalendarType.values().forEach {
                    DropdownMenuItem(onClick = {
                        onCalendarSelected(it); dropdownExpanded = false
                    }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(it.icon, it.icon.name,
                                //modifier = Modifier.fillMaxWidth(0.2f)
                                )
                            Text(
                                text = stringResource(id = it.stringRes),
                                maxLines = 1,
                                //modifier = Modifier.fillMaxWidth(0.8f)
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EventOptionDialog(
    calendarEvent: CalendarEvent?,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    calendarEvent?.let {
        DefaultDialog(primaryText = calendarEvent.appliance?.name ?: stringResource(id = R.string.appliance_name_failed),
            secondaryText = "${calendarEvent.timeStart.format(EventTimeFormatter)} - ${
                calendarEvent.timeEnd.format(
                    EventTimeFormatter
                )
            }\n${calendarEvent.commentary}",
            onDismiss = onDismiss,
            neutralButtonText = stringResource(id = R.string.delete),
            onNeutralClick = { onDelete(calendarEvent.id); onDismiss() }
        )
    }
}

package ru.dvfu.appliances.compose.calendars

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.appliance.NoElementsView
import ru.dvfu.appliances.compose.event_calendar.EventTimeFormatter
import ru.dvfu.appliances.compose.home.HomeTopBar
import ru.dvfu.appliances.compose.home.SelectedDate
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.viewmodels.EventsState
import ru.dvfu.appliances.compose.viewmodels.WeekCalendarViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.utils.Constants
import ru.dvfu.appliances.model.utils.loadingModifier
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MonthWeekCalendar(
    viewModel: WeekCalendarViewModel,
    navController: NavController,
    onEventClick: (CalendarEvent) -> Unit,
    onEventLongClick: (CalendarEvent) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val currentDate by viewModel.currentDate.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val dayEvents = viewModel.dayEvents
    val scrollState = rememberScrollState()

    val backdropScaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed)

    BackHandler(backdropScaffoldState.isConcealed) {
        coroutineScope.launch {
            backdropScaffoldState.animateTo(BackdropValue.Revealed)
        }
    }

    val calendarState = rememberSelectableCalendarState(
        initialSelection = listOf(currentDate),
        onSelectionChanged = viewModel::onDateSelectionChanged,
    )

    LaunchedEffect(calendarState.monthState.currentMonth) {
        viewModel.onMonthChanged(calendarState.monthState.currentMonth)
    }


    Scaffold(
        floatingActionButton = {
            if (!currentUser.isAnonymousOrGuest()) {
                FloatingActionButton(backgroundColor = Color(0xFFFF8C00),
                    onClick = {
                        navController.navigate(
                            MainDestinations.ADD_EVENT,
                            Arguments.DATE to SelectedDate(currentDate)
                        )
                    }) { Icon(Icons.Default.Add, "") }
            }
        },
    ) { it ->
        BackdropScaffold(
            appBar = {
                HomeTopBar(uiState = uiState, onBookingListOpen = {
                    navController.navigate(MainDestinations.BOOKING_LIST)
                }, onCalendarSelected = viewModel::setCalendarType)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            scaffoldState = backdropScaffoldState,
            backLayerBackgroundColor = MaterialTheme.colors.surface,
            frontLayerElevation = 16.dp,
            frontLayerScrimColor = MaterialTheme.colors.surface.copy(alpha = 0f),
            frontLayerBackgroundColor = Color(0XFFE3DAC9),
            backLayerContent = {
                SelectableCalendar(
                    modifier = Modifier.padding(8.dp),
                    calendarState = calendarState,
                    dayContent = { dayState ->
                        ScheduleCalendarDate(
                            currentUser = currentUser,
                            state = dayState,
                            currentDayEvents = (dayEvents[dayState.date] as? EventsState.Loaded)?.events.orEmpty()
                        )
                    },
                    monthHeader = { SchedulerMonthHeader(it) }
                )
            },
            frontLayerContent = {
                dayEvents[currentDate]?.let { state ->
                    Crossfade(state) { eventsState ->
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .padding(8.dp)
                                .padding(bottom = 150.dp)
                        ) {
                            when (eventsState) {
                                is EventsState.Loaded -> {
                                    if (eventsState.events.isEmpty()) {
                                        NoElementsView(mainText = "Нет событий на выбранный день") {}
                                    }
                                    eventsState.events.forEach { event ->
                                        EventView(
                                            onEventClick = onEventClick,
                                            onEventLongClick = onEventLongClick,
                                            event = event
                                        )
                                    }
                                }
                                EventsState.Loading -> {
                                    (0..2).forEach {
                                        EventView(
                                            childModifier = Modifier.loadingModifier(),
                                            event = CalendarEvent(
                                                appliance = Appliance(
                                                    name = "Appliance",
                                                    color = Color.White.copy(0f).hashCode()
                                                ),
                                                date = LocalDate.now(),
                                                timeCreated = LocalDateTime.now(),
                                                timeStart = LocalDateTime.now(),
                                                timeEnd = LocalDateTime.now(),
                                                status = BookingStatus.APPROVED
                                            ), onEventClick = {}, onEventLongClick = {})
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventView(
    modifier: Modifier = Modifier,
    childModifier: Modifier = Modifier,
    onEventClick: (CalendarEvent) -> Unit,
    onEventLongClick: (CalendarEvent) -> Unit,
    event: CalendarEvent
) {
    val contentAlpha = when (event.status) {
        BookingStatus.NONE -> {
            ContentAlpha.disabled
        }
        else -> ContentAlpha.high
    }
    CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp)
                .clipToBounds()
                .background(
                    when (event.status) {
                        BookingStatus.NONE -> {
                            event.appliance.color?.let { Color(it).copy(alpha = 0.5f) }
                                ?: Constants.DEFAULT_EVENT_COLOR.copy(alpha = 0.5f)
                        }
                        else -> event.appliance?.color?.let { Color(it) }
                            ?: Constants.DEFAULT_EVENT_COLOR
                    },
                    shape = RoundedCornerShape(4.dp)
                )
                .combinedClickable(
                    onClick = { onEventClick(event) },
                    onLongClick = { onEventLongClick(event) }
                )
                .then(childModifier)
        ) {
            Column(Modifier.padding(4.dp)) {
                Text(
                    text = "${event.timeStart.format(EventTimeFormatter)} - ${
                        event.timeEnd.format(EventTimeFormatter)
                    }",
                    style = MaterialTheme.typography.caption,
                    maxLines = 2,
                    overflow = TextOverflow.Clip,
                    modifier = childModifier
                )

                Text(
                    text = event.appliance?.name
                        ?: stringResource(id = R.string.appliance_name_failed),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = childModifier
                )

                if (event.commentary.isNotBlank()) {
                    Text(
                        text = event.commentary,
                        style = MaterialTheme.typography.body2,

                        overflow = TextOverflow.Ellipsis,
                        modifier = childModifier
                    )
                }
            }

        }
    }
}



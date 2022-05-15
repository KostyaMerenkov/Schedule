package ru.dvfu.appliances.compose.calendars

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import kotlinx.coroutines.launch
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.appliance.NoElementsView
import ru.dvfu.appliances.compose.home.HomeTopBar
import ru.dvfu.appliances.compose.home.SelectedDate
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.viewmodels.EventsState
import ru.dvfu.appliances.compose.viewmodels.WeekCalendarViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.formattedTime
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
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (!currentUser.isAnonymousOrGuest()) {
                FloatingActionButton(
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
                                .filter { it.status != BookingStatus.DECLINED }
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
                                            event = event,
                                            currentUser = currentUser
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
                                            ),
                                            currentUser = User(),
                                            onEventClick = {},
                                            onEventLongClick = {})
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
    event: CalendarEvent,
    currentUser: User
) {
    val contentAlpha = when (event.status) {
        BookingStatus.DECLINED -> {
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
                        BookingStatus.DECLINED -> {
                            Color(event.appliance.color).copy(alpha = 0.5f)
                        }
                        else -> Color(event.appliance.color)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = formattedTime(event.timeStart, event.timeEnd),
                            style = MaterialTheme.typography.caption,
                            maxLines = 2,
                            overflow = TextOverflow.Clip,
                            modifier = childModifier
                        )
                        Text(
                            text = event.appliance.name,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = childModifier
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(Icons.Default.AccountCircle, "")
                            Text(
                                text = event.user.userName,
                                style = MaterialTheme.typography.body1,
                                //fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = childModifier
                            )
                        }
                    }
                    if (event.status == BookingStatus.NONE) {
//                        if (currentUser.canManageEvent(event)) {
//                            Row {
//                                IconButton(onClick = {  }) {
//                                    Icon(Icons.Default.Close, "")
//                                }
//                                IconButton(onClick = {  }) {
//                                    Icon(Icons.Default.Done, "")
//                                }
//                            }
//                        } else {
                        Icon(event.status.icon, "status"/*, tint = event.status.color*/)
//                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colors.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(event.status.icon, "status", tint = event.status.color)
                        }
                    }

                }

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



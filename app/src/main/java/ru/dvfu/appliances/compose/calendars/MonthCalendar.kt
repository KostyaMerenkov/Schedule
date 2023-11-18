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
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.appliance.NoElementsView
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.components.views.ModalLoadingDialog
import ru.dvfu.appliances.compose.home.HomeTopBar
import ru.dvfu.appliances.compose.home.SelectedDate
import ru.dvfu.appliances.compose.home.booking_list.BookingCommentaryDialog
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.viewmodels.EventsState
import ru.dvfu.appliances.compose.viewmodels.WeekCalendarViewModel
import ru.dvfu.appliances.model.repository.entity.*
import ru.dvfu.appliances.model.utils.formattedTime
import ru.dvfu.appliances.model.utils.loadingModifier
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MonthWeekCalendar(
    viewModel: WeekCalendarViewModel,
    navController: NavController,
    onEventClick: (CalendarEvent) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val currentDate by viewModel.currentDate.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val managingUiState by viewModel.managingUiState.collectAsState()
    val dayEvents = viewModel.dayEvents
    val scrollState = rememberScrollState()

    val backdropScaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed)

    val calendarState = rememberSelectableCalendarState(
        initialSelection = listOf(currentDate),
        onSelectionChanged = viewModel::onDateSelectionChanged
    )

    BackHandler(backdropScaffoldState.isConcealed || calendarState.monthState.currentMonth != YearMonth.now()) {
        if (backdropScaffoldState.isConcealed) {
            coroutineScope.launch {
                backdropScaffoldState.animateTo(BackdropValue.Revealed)
            }
        } else { calendarState.monthState.currentMonth = YearMonth.now() }
    }

    LaunchedEffect(calendarState.monthState.currentMonth) {
        viewModel.onMonthChanged(calendarState.monthState.currentMonth)
    }

    if (managingUiState is UiState.InProgress) { ModalLoadingDialog() }

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (!currentUser.isAnonymousOrGuest) {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate(
                            MainDestinations.ADD_EVENT,
                            Arguments.DATE to SelectedDate(currentDate)
                        )
                    },
                    text = {
                        Text(text = stringResource(id = R.string.new_event))
                    })
            }
        },
    ) { it ->
        BackdropScaffold(
            appBar = {
                HomeTopBar(
                    uiState = uiState,
                    onBookingListOpen = {
                        navController.navigate(MainDestinations.BOOKING_LIST)
                    },
                    onCalendarSelected = viewModel::setCalendarType,
                    onRetry = { viewModel.onMonthChanged(calendarState.monthState.currentMonth) }
                )
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
                                .filter { it.status != BookingStatus.DECLINED && it.appliance.active }
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
                                            uiState = uiState,
                                            event = event,
                                            currentUser = currentUser,
                                            onApproveClick = viewModel::onApproveClick,
                                            onDeclineClick = viewModel::onDeclineClick
                                        )
                                    }
                                }
                                EventsState.Loading -> {
                                    (0..2).forEach {
                                        EventView(
                                            childModifier = Modifier.loadingModifier(),
                                            uiState = uiState,
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
                                            onApproveClick = { _, _ -> },
                                            onDeclineClick = { _, _ -> })
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventView(
    modifier: Modifier = Modifier,
    childModifier: Modifier = Modifier,
    uiState: UiState,
    onEventClick: (CalendarEvent) -> Unit,
    onApproveClick: (CalendarEvent, String) -> Unit,
    onDeclineClick: (CalendarEvent, String) -> Unit,
    event: CalendarEvent,
    currentUser: User
) {
    val contentAlpha = when (event.status) {
        BookingStatus.DECLINED -> ContentAlpha.disabled
        else -> ContentAlpha.high
    }

    var approveDialogState by remember { mutableStateOf(false) }
    var declineDialogState by remember { mutableStateOf(false) }

    if (approveDialogState) {
        BookingCommentaryDialog(
            commentArg = "",
            onCancel = { approveDialogState = false },
            onApplyCommentary = {
                approveDialogState = false
                onApproveClick(event, it)
            },
            newStatus = BookingStatus.APPROVED
        )
    }

    if (declineDialogState) {
        BookingCommentaryDialog(
            commentArg = "",
            onCancel = { declineDialogState = false },
            onApplyCommentary = {
                approveDialogState = false
                onDeclineClick(event, it)
            },
            newStatus = BookingStatus.DECLINED
        )
    }

    CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .width(12.dp)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        color = Color(event.appliance.color).copy(alpha = LocalContentAlpha.current),
                        CircleShape
                    )
                    .then(childModifier)
            )
            Card(
                elevation = 0.dp,
                shape = RoundedCornerShape(8.dp),
                modifier = modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clipToBounds()
                    .then(childModifier),
                onClick = { onEventClick(event) }
            ) {
                Column(Modifier.padding(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                            modifier = Modifier.weight(1f, false)
                        ) {
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

                        }
                        if (event.status == BookingStatus.NONE && currentUser.canManageEvent(event)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { declineDialogState = true },
                                    shape = CircleShape,
                                    enabled = uiState !is UiState.InProgress
                                ) {
                                    Icon(Icons.Default.Close, "")
                                }
                                OutlinedButton(
                                    onClick = { approveDialogState = true },
                                    shape = CircleShape,
                                    enabled = uiState !is UiState.InProgress
                                ) {
                                    Icon(Icons.Default.Done, "")
                                }
                            }
                        } else {
                            IconButtonWithoutOnClick(modifier = Modifier.padding(start = 4.dp)) {
                                Icon(event.status.icon, "status", tint = event.status.color)
                            }
                        }
                    }

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
}

@Composable
fun IconButtonWithoutOnClick(modifier: Modifier = Modifier, function: @Composable () -> Unit) {
    Box(
        modifier = Modifier.size(LocalViewConfiguration.current.minimumTouchTargetSize),
        contentAlignment = Alignment.Center
    ) {
        function()
    }
}



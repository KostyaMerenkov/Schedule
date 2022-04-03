package ru.dvfu.appliances.compose.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.components.FabMenuItem
import ru.dvfu.appliances.compose.components.FabWithMenu
import ru.dvfu.appliances.compose.components.MultiFabState
import ru.dvfu.appliances.compose.event_calendar.CalendarEvent
import ru.dvfu.appliances.compose.event_calendar.EventTimeFormatter
import ru.dvfu.appliances.compose.event_calendar.Schedule
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.views.DefaultDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Composable
fun MainScreen(navController: NavController, openDrawer: () -> Unit) {
    val viewModel: MainScreenViewModel = getViewModel()
    val events by viewModel.events.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var eventOptionDialogOpened by remember { mutableStateOf(false) }
    if (eventOptionDialogOpened) EventOptionDialog(
        calendarEvent = viewModel.selectedEvent.value,
        onDismiss = { eventOptionDialogOpened = false },
        onDelete = viewModel::deleteEvent
    )

    val fabState = remember { mutableStateOf(MultiFabState.COLLAPSED) }

    Scaffold(topBar = {
        HomeTopBar(onOpenDrawer = openDrawer, onBookingListOpen = {
            navController.navigate(MainDestinations.BOOKING_LIST)
        })
    }, floatingActionButton = {
        FabWithMenu(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .zIndex(5f),
            fabState = fabState,
            items = listOf(
                //if(currentUser.isAdmin()) {
                    FabMenuItem(
                        icon = Icons.Default.AddTask,
                        text = "Создать событие",
                        onClick = { navController.navigate(MainDestinations.ADD_EVENT) }
                    ),
                //},
                FabMenuItem(
                    icon = Icons.Default.MoreTime,
                    text = "Создать бронирование",
                    onClick = {
                        navController.navigate(MainDestinations.ADD_BOOKING)
                    }
                ),

            )
        )
    }
    ) {
        AnimatedVisibility(
            fabState.value == MultiFabState.EXPANDED,
            modifier = Modifier
                .zIndex(4f)
                .fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(0.6f))
                    .clickable(role = Role.Image) {
                        fabState.value = MultiFabState.COLLAPSED
                    })
        }
        val verticalScrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()

        Schedule(calendarEvents = events, minDate = LocalDate.now().minusDays(1),
            maxDate = LocalDate.now().plusDays(6),
            onEventClick = {
                viewModel.getRepoEvent(it)?.let {
                    navController.navigate(
                        MainDestinations.EVENT_INFO,
                        Arguments.EVENT to it
                    )
                }
            },
            onEventLongClick = {
                viewModel.selectedEvent.value = it
                eventOptionDialogOpened = true
            },
            verticalScrollState = verticalScrollState,
            horizontalScrollState = horizontalScrollState
        )

        DisposableEffect(Unit) {
            coroutineScope.launch {
                horizontalScrollState.scrollTo(2)
            }
            onDispose {  }
        }
    }
}

@Composable
fun HomeTopBar(onOpenDrawer: () -> Unit, onBookingListOpen: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.schedule)) },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Filled.Menu, contentDescription = "")
            }
        },
        backgroundColor = Color(0xFFFF5470),
        actions = {
            IconButton(onClick = onBookingListOpen) {
                Icon(Icons.Default.Book, Icons.Default.Book.name)
            }
        }
    )

    /*ScheduleAppBar(

    )*/
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EventOptionDialog(
    calendarEvent: CalendarEvent?,
    onDelete: (CalendarEvent) -> Unit,
    onDismiss: () -> Unit
) {

    calendarEvent?.let {
        DefaultDialog(primaryText = calendarEvent.applianceName,
            secondaryText = "${calendarEvent.start.format(EventTimeFormatter)} - ${
                calendarEvent.end.format(
                    EventTimeFormatter
                )
            }\n${calendarEvent.description}",
            onDismiss = onDismiss,
            neutralButtonText = stringResource(id = R.string.delete),
            onNeutralClick = { onDelete(calendarEvent); onDismiss() }
        )
    }
}

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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
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
    val context = LocalContext.current

    var eventOptionDialogOpened by remember { mutableStateOf(false) }
    if (eventOptionDialogOpened) EventOptionDialog(
        calendarEvent = viewModel.selectedEvent.value,
        onDismiss = { eventOptionDialogOpened = false },
        onDelete = viewModel::deleteEvent
    )

    val fabState = remember { mutableStateOf(MultiFabState.COLLAPSED) }

    Scaffold(topBar = {
        HomeTopBar(onOpenDrawer = openDrawer)
    }, floatingActionButton = {
        FabWithMenu(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .zIndex(5f),
            fabState = fabState,
            items = listOf(
                FabMenuItem(
                    icon = Icons.Default.MoreTime,
                    text = "Создать бронирование",
                    onClick = {
                        Toast.makeText(
                            context.applicationContext,
                            "Еще не готово!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ),
                FabMenuItem(
                    icon = Icons.Default.AddTask,
                    text = "Создать событие",
                    onClick = { navController.navigate(MainDestinations.ADD_EVENT) }
                )
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

        Schedule(calendarEvents = events, minDate = LocalDate.now().minusDays(1),
            maxDate = LocalDate.now().plusDays(1),
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
            })
    }
}

@Composable
fun HomeTopBar(onOpenDrawer: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.schedule)) },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Filled.Menu, contentDescription = "")
            }
        },
        backgroundColor = Color(0xFFFF5470)
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

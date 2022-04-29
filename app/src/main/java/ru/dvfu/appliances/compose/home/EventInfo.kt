package ru.dvfu.appliances.compose.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.*
import ru.dvfu.appliances.compose.components.TimePicker
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.event_calendar.EventTimeFormatter
import ru.dvfu.appliances.compose.home.booking_list.BookingAppliance
import ru.dvfu.appliances.compose.home.booking_list.EventInfoScreen
import ru.dvfu.appliances.compose.viewmodels.EventInfoViewModel
import ru.dvfu.appliances.compose.views.DefaultDialog
import ru.dvfu.appliances.compose.views.HeaderText
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.toLocalTime
import ru.dvfu.appliances.ui.ViewState
import java.time.LocalTime

@Composable
fun EventInfo(navController: NavController, eventArg: CalendarEvent, backPress: () -> Unit) {

    val viewModel: EventInfoViewModel = getViewModel(parameters = { parametersOf(eventArg) })
    val eventDeleteState by viewModel.eventDeleteState.collectAsState()
    val applianceState by viewModel.applianceState.collectAsState()
    val event by viewModel.event.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val superUserState by viewModel.userState.collectAsState()
    val canUpdate by viewModel.canUpdate.collectAsState()
    val timeEndChangeState by viewModel.timeEndChangeState.collectAsState()
    val timeStartChangeState by viewModel.timeStartChangeState.collectAsState()

    val scrollState = rememberScrollState()

    var eventDeleteDialog by remember { mutableStateOf(false) }
    if (eventDeleteDialog) {
        EventDeleteDialog(onDismiss = { eventDeleteDialog = false }) { viewModel.deleteEvent() }
    }

    LaunchedEffect(eventDeleteState) {
        when (eventDeleteState) {
            UiState.Error -> {
                SnackbarManager.showMessage(R.string.event_delete_failed)
            }
            UiState.Success -> {
                SnackbarManager.showMessage(R.string.event_delete_successfully)
                backPress()
            }
            else -> {}
        }
    }

    var timeEndEditDialog by remember { mutableStateOf(false) }
    if (timeEndEditDialog) {
        TimeEditDialog(
            eventTimeEnd = event.timeEnd.toLocalTime(),
            onDismiss = { timeEndEditDialog = false },
            onTimeChange = viewModel::onTimeEndChange
        )
    }

    var timeStartEditDialog by remember { mutableStateOf(false) }
    if (timeStartEditDialog) {
        TimeEditDialog(
            eventTimeEnd = event.timeStart.toLocalTime(),
            onDismiss = { timeStartEditDialog = false },
            onTimeChange = viewModel::onTimeStartChange
        )
    }


    Scaffold(topBar = {
        EventInfoTopBar(
            couldDeleteEvent = viewModel.couldDeleteEvent.collectAsState(),
            backPress
        ) { eventDeleteDialog = true }
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EventInfoScreen(
                currentUser = currentUser,
                navController = navController,
                event = event,
                onApproveClick = {},
                onDeclineClick = {},
                onSetDateAndTime = {}
            )
        }
    }


}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EventDeleteDialog(onDismiss: () -> Unit, function: () -> Unit) {
    DefaultDialog(
        primaryText = stringResource(id = R.string.event_delete_sure),
        positiveButtonText = stringResource(id = R.string.Yes),
        negativeButtonText = stringResource(id = R.string.No),
        onPositiveClick = { function(); onDismiss() },
        onNegativeClick = { onDismiss() },
        onDismiss = onDismiss
    ) {}
}

@Composable
fun EventInfoFAB(uiState: UiState?, onSave: () -> Unit) {
    FloatingActionButton(onClick = onSave) {
        Crossfade(targetState = uiState) {
            when (it) {
                is UiState.InProgress -> {
                    CircularProgressIndicator()
                }
                else -> Icon(Icons.Default.Save, "")
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeEditDialog(
    eventTimeEnd: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    TimePicker(
        time = eventTimeEnd,
        onTimeSet = onTimeChange,
        context = context,
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun EventAppliance(applianceState: ViewState<Appliance>, applianceClicked: (Appliance) -> Unit) {
    Crossfade(targetState = applianceState) {
        when (it) {
            is ViewState.Error -> TODO()
            is ViewState.Loading -> {
                CircularProgressIndicator()
            }
            is ViewState.Success -> {
                MyCard {
                    BookingAppliance(
                        appliance = it.data, onApplianceClick = { applianceClicked(it.data) },
                        shouldShowHeader = false
                    )
                }
            }
        }
    }
}

@Composable
fun EventInfoTopBar(couldDeleteEvent: State<Boolean>, upPress: () -> Unit, onDelete: () -> Unit) {
    ScheduleAppBar(
        backClick = upPress,
        actionDelete = couldDeleteEvent.value,
        deleteClick = onDelete,
        elevation = 0.dp
    )
}

@OptIn(
    ExperimentalCoilApi::class, androidx.compose.material.ExperimentalMaterialApi::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class
)
@Composable
fun EventUser(userState: ViewState<User>, onUserClicked: (User) -> Unit) {
    Crossfade(targetState = userState) {
        when (it) {
            is ViewState.Error -> TODO()
            is ViewState.Loading -> {
                CircularProgressIndicator()
            }
            is ViewState.Success -> {
                ItemUser(
                    user = it.data,
                    userClicked = { onUserClicked(it.data) },
                )
            }
        }
    }

}

package ru.dvfu.appliances.compose.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
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
import ru.dvfu.appliances.compose.viewmodels.EventInfoViewModel
import ru.dvfu.appliances.compose.views.DefaultDialog
import ru.dvfu.appliances.compose.views.HeaderText
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.model.utils.toLocalTime
import ru.dvfu.appliances.ui.ViewState
import java.time.LocalTime

@Composable
fun EventInfo(navController: NavController, eventArg: Event, backPress: () -> Unit) {

    val viewModel: EventInfoViewModel = getViewModel(parameters = { parametersOf(eventArg) })
    val eventDeleteState by viewModel.eventDeleteState.collectAsState()
    val applianceState by viewModel.applianceState.collectAsState()
    val event by viewModel.event.collectAsState()
    val userState by viewModel.userState.collectAsState()
    val superUserState by viewModel.userState.collectAsState()
    val canUpdate by viewModel.canUpdate.collectAsState()

    var eventDeleteDialog by remember { mutableStateOf(false) }
    if (eventDeleteDialog) {
        EventDeleteDialog(onDismiss = { eventDeleteDialog = false }) {
            viewModel.deleteEvent()
        }
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

    var timeEditDialog by remember { mutableStateOf(false) }
    if (timeEditDialog) {
        TimeEditDialog(event = event, onDismiss = { timeEditDialog = false }, onTimeChange = {

        })
    }
    Scaffold(
        topBar = {
            EventInfoTopBar(backPress) { eventDeleteDialog = true }
        },
        floatingActionButton = {
            if (canUpdate) {
                EventInfoFAB(eventDeleteState, onSave = viewModel::saveChanges)
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderText(text = stringResource(id = R.string.time))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = eventArg.timeStart.toLocalTime().format(EventTimeFormatter),
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.timeStart)) },
                    readOnly = true,
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = eventArg.timeEnd.toLocalTime().format(EventTimeFormatter),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.timeEnd)) },
                    trailingIcon = {
                        IconButton(onClick = { timeEditDialog = true }) {
                            Icon(Icons.Default.Edit, Icons.Default.Edit.name)
                        }
                    })
            }
            HeaderText(text = stringResource(id = R.string.appliance))
            EventAppliance(applianceState) {
                navController.navigate(
                    MainDestinations.APPLIANCE_ROUTE,
                    Arguments.APPLIANCE to it
                )
            }
            HeaderText(text = stringResource(id = R.string.user))
            EventUser(userState) {
                navController.navigate(
                    MainDestinations.USER_DETAILS_ROUTE,
                    Arguments.USER to it
                )
            }
            HeaderText(text = stringResource(id = R.string.superuser))
            EventUser(superUserState) {
                navController.navigate(
                    MainDestinations.USER_DETAILS_ROUTE,
                    Arguments.USER to it
                )
            }




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
fun TimeEditDialog(event: Event, onTimeChange: (LocalTime) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    TimePicker(
        time = event.timeEnd.toLocalTime(),
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
                ItemAppliance(appliance = it.data, applianceClicked = applianceClicked)
            }
        }
    }

}

@Composable
fun EventInfoTopBar(upPress: () -> Unit, onDelete: () -> Unit) {
    ScheduleAppBar(
        "Событие"/*stringResource(R.string.event_info)*/,
        backClick = upPress,
        actionDelete = true,
        deleteClick = onDelete,
        elevation = 0.dp
    )
}

@OptIn(ExperimentalCoilApi::class, androidx.compose.material.ExperimentalMaterialApi::class,
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

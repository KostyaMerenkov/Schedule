package ru.dvfu.appliances.compose.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import ru.dvfu.appliances.R
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.*
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.home.booking_list.EventInfo
import ru.dvfu.appliances.compose.viewmodels.EventInfoViewModel
import ru.dvfu.appliances.compose.components.views.DefaultDialog
import ru.dvfu.appliances.compose.components.views.ModalLoadingDialog
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.canManageEvent

@Composable
fun EventInfoScreen(navController: NavController, eventArg: CalendarEvent, backPress: () -> Unit) {

    val viewModel: EventInfoViewModel = getViewModel(parameters = { parametersOf(eventArg) })
    val eventDeleteState by viewModel.eventDeleteState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val scrollState = rememberScrollState()

    var eventDeleteDialog by remember { mutableStateOf(false) }
    if (eventDeleteDialog) {
        EventDeleteDialog(onDismiss = { eventDeleteDialog = false }) { viewModel.deleteEvent() }
    }

    if (uiState is UiState.InProgress) ModalLoadingDialog()

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

    Scaffold(topBar = {
        EventInfoTopBar(
            currentUser.canManageEvent(event),
            backPress
        ) { eventDeleteDialog = true }
    }) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EventInfo(
                currentUser = currentUser,
                navController = navController,
                event = event,
                onApproveClick = viewModel::onApproveClick,
                onDeclineClick = viewModel::onDeclineClick,
                onSetDateAndTime = viewModel::onSetDateAndTime,
                onCommentarySave = viewModel::onCommentarySave,
                onUserRefuseClick = {_, _ -> }
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
fun EventInfoTopBar(couldDeleteEvent: Boolean, upPress: () -> Unit, onDelete: () -> Unit) {
    ScheduleAppBar(
        title = stringResource(id = R.string.booking),
        backClick = upPress,
        actionDelete = couldDeleteEvent,
        deleteClick = onDelete,
        elevation = 0.dp
    )
}

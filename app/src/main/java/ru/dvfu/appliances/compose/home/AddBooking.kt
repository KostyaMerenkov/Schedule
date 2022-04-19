package ru.dvfu.appliances.compose.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import org.koin.core.parameter.parametersOf
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.appliance.FabWithLoading
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.viewmodels.AddEventViewModel
import java.time.LocalDate

@Composable
fun AddBooking(selectedDate: LocalDate, navController: NavController) {
    val viewModel: AddEventViewModel = get(parameters = { parametersOf(selectedDate) })
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> navController.popBackStack()
            else -> {}
        }
    }

    Scaffold(topBar = {
        ScheduleAppBar(title = "Создание бронирования", backClick = navController::popBackStack)
    },
        floatingActionButton = {
            FabWithLoading(
                showLoading = uiState is UiState.InProgress,
                onClick = { /*viewModel.createBooking()*/ }) {
                Icon(Icons.Default.Check, contentDescription = Icons.Default.Check.name)
            }
        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState, enabled = true)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            /*DateAndTime(
                date = viewModel.date.collectAsState().value,
                timeStart = viewModel.timeStart.collectAsState().value.toLocalTime(),
                timeEnd = viewModel.timeEnd.collectAsState().value.toLocalTime(),
                onDateSet = viewModel::onDateSet,
                onTimeStartSet = viewModel::onTimeStartSet,
                onTimeEndSet = viewModel::onTimeEndSet,
                duration = viewModel.duration.collectAsState().value,
                isDurationError = viewModel.isDurationError.collectAsState().value,
            )*/
            /*Commentary(
                commentary = viewModel.commentary.collectAsState().value,
                onCommentarySet = viewModel::onCommentarySet
            )*/
            ChooseAppliance(
                appliancesState = viewModel.appliancesState.collectAsState().value,
                selectedAppliance = viewModel.selectedAppliance.collectAsState(),
                onApplianceSelected = viewModel::onApplianceSelected
            )
        }
    }
}
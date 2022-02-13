package ru.dvfu.appliances.compose.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.AppliancesLazyRow
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.components.DatePicker
import ru.dvfu.appliances.compose.components.TimePicker
import ru.dvfu.appliances.compose.components.toDate
import ru.dvfu.appliances.compose.components.toTime
import ru.dvfu.appliances.compose.viewmodels.AddEventViewModel
import java.util.*

@Composable
fun AddEvent(navController: NavController) {
    val viewModel: AddEventViewModel = get()
    val scrollState = rememberScrollState()

    val calendar = remember { Calendar.getInstance() }

    LaunchedEffect(key1 = null) {
        viewModel.date.value = calendar.timeInMillis
        viewModel.timeStart.value = calendar.timeInMillis
        viewModel.timeEnd.value = calendar.timeInMillis
    }

    Scaffold(topBar = {
        ScheduleAppBar(title = "Добавление события", backClick = navController::popBackStack)
    }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState, enabled = true)
                .padding(horizontal = 16.dp, vertical = 12.dp)

        ) {
            DateAndTime(viewModel)
        }
    }

}

@Composable
fun DateAndTime(viewModel: AddEventViewModel) {
    val context = LocalContext.current
    val duration by viewModel.duration.collectAsState()

    val dateSetState = remember { mutableStateOf(false) }
    val timeSetStartState = remember { mutableStateOf(false) }
    val timeSetEndState = remember { mutableStateOf(false) }

    if (dateSetState.value) DatePicker(viewModel.date, dateSetState, context)
    if (timeSetStartState.value) TimePicker(viewModel.timeStart, timeSetStartState, context)
    if (timeSetEndState.value) TimePicker(viewModel.timeEnd, timeSetEndState, context)

    LaunchedEffect(timeSetEndState.value, timeSetStartState.value) {
        viewModel.getDuration()
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {


        OutlinedTextField(
            value = viewModel.date.value.toDate(),
            onValueChange = {},
            label = { Text(text = stringResource(R.string.date)) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    dateSetState.value = true
                }) {
                    Icon(
                        Icons.Default.Today,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = stringResource(R.string.date)
                    )
                }

            })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            OutlinedTextField(
                value = viewModel.timeStart.value.toTime(),
                onValueChange = {},
                label = { Text(text = stringResource(R.string.time_start)) },
                readOnly = true,
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = {
                        timeSetStartState.value = true
                    }) {
                        Icon(
                            Icons.Default.AccessTime,
                            tint = MaterialTheme.colors.primary,
                            contentDescription = stringResource(R.string.time)
                        )
                    }

                })
            OutlinedTextField(
                value = viewModel.timeEnd.value.toTime(),
                onValueChange = {},
                label = { Text(text = stringResource(R.string.time_end)) },
                readOnly = true,
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = {
                        timeSetEndState.value = true
                    }) {
                        Icon(
                            Icons.Default.AccessTime,
                            tint = MaterialTheme.colors.primary,
                            contentDescription = stringResource(R.string.time)
                        )
                    }

                })

        }
        Text(text = "Продолжительность: $duration")
        //AppliancesLazyRow(appliances = , navController = )
    }
}

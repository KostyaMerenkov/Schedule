package ru.dvfu.appliances.compose.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.MyCard
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.appliance.FabWithLoading
import ru.dvfu.appliances.compose.components.*
import ru.dvfu.appliances.compose.utils.TimeConstants.MILLISECONDS_IN_HOUR
import ru.dvfu.appliances.compose.viewmodels.AddEventViewModel
import ru.dvfu.appliances.compose.views.PrimaryText
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.ViewState
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Composable
fun AddEvent(navController: NavController) {
    val viewModel: AddEventViewModel = get()
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> navController.popBackStack()
            else -> {}
        }
    }

    val calendar = remember { Calendar.getInstance() }

    LaunchedEffect(key1 = null) {
        viewModel.date.value = calendar.timeInMillis
        viewModel.timeStart.value = calendar.timeInMillis
        viewModel.timeEnd.value = calendar.timeInMillis
    }

    Scaffold(topBar = {
        ScheduleAppBar(title = "Создание события", backClick = navController::popBackStack)
    },
        floatingActionButton = {
            FabWithLoading(showLoading = uiState is UiState.InProgress,
                onClick = { viewModel.addEvent() }) {
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
            DateAndTime(
                date = viewModel.date.value,
                timeStart = viewModel.timeStart.value,
                timeEnd = viewModel.timeEnd.value,
                onDateSet = viewModel::onDateSet,
                onTimeStartSet = viewModel::onTimeStartSet,
                onTimeEndSet = viewModel::onTimeEndSet,
                duration = viewModel.duration.collectAsState().value,
                isDurationError = viewModel.isDurationError.collectAsState().value,
            )
            Commentary(commentary = viewModel.commentary.value, onCommentarySet = viewModel::onCommentarySet)
            ChooseAppliance(
                appliancesState = viewModel.appliancesState.collectAsState().value,
                selectedAppliance = viewModel.selectedAppliance.collectAsState(),
                onApplianceSelected = viewModel::onApplianceSelected
            )
        }
    }

}

@Composable
fun Commentary(modifier: Modifier = Modifier, commentary: String, onCommentarySet: (String) -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PrimaryText(
            text = stringResource(id = R.string.commentary),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = commentary, onValueChange = onCommentarySet,
            textStyle = TextStyle(color = MaterialTheme.colors.onSurface, fontSize = 16.sp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ChooseAppliance(
    appliancesState: ViewState<List<Appliance>>,
    selectedAppliance: State<Appliance?>,
    onApplianceSelected: (Appliance) -> Unit
) {
    when (appliancesState) {
        is ViewState.Success -> {
            Column {
                PrimaryText(
                    text = stringResource(id = R.string.choose_appliance),
                    modifier = Modifier.fillMaxWidth()
                )
                ApplianceSelection(radioOptions = appliancesState.data,
                    currentOption = selectedAppliance,
                    onSelectedItem = {
                        onApplianceSelected(it)
                    })
            }
        }
        is ViewState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemApplianceSelectable(
    appliance: Appliance,
    isSelected: Boolean,
    applianceClicked: () -> Unit
) {
    val border = animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colors.primary else Color.White.copy(0f)
    )
    val selectedColor = animateColorAsState(
        targetValue = if (isSelected) Color.LightGray else MaterialTheme.colors.surface
    )
    val borderModifier =
        if (isSelected) Modifier.border(2.dp, border.value, CircleShape) else Modifier.border(
            1.dp,
            Color.Black,
            CircleShape
        )
    MyCard(
        onClick = applianceClicked, modifier = Modifier,
        backgroundColor = selectedColor.value
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(180.dp)
                .width(120.dp)
                .padding(5.dp)

        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .requiredSize(100.dp)
                    .clip(CircleShape)
                    .background(Color(appliance.color))
                    .then(borderModifier),
            ) {
                //Crossfade(targetState = isSelected) {
                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = Icons.Default.Check.name)
                } else {
                    Text(
                        if (appliance.name.isEmpty()) ""
                        else appliance.name.first().uppercase(),
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.h4,
                    )
                }
                //}
            }
            Text(
                appliance.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun DateAndTime(
    date: Long,
    timeStart: Long,
    timeEnd: Long,
    onDateSet: (Long) -> Unit,
    onTimeStartSet: (Long) -> Unit,
    onTimeEndSet: (Long) -> Unit,
    duration: String,
    isDurationError: Boolean,
) {
    val context = LocalContext.current

    val dateSetState = remember { mutableStateOf(false) }
    val timeSetStartState = remember { mutableStateOf(false) }
    val timeSetEndState = remember { mutableStateOf(false) }

    if (dateSetState.value) DatePicker(context, date, onDateSet = onDateSet) {
        dateSetState.value = false
    }
    if (timeSetStartState.value) TimePicker(
        context, timeStart,
        onTimeSet = onTimeStartSet
    ) { timeSetStartState.value = false }
    if (timeSetEndState.value) TimePicker(
        context, timeEnd,
        onTimeSet = onTimeEndSet,
    ) { timeSetEndState.value = false }

    LaunchedEffect(timeStart) {
        onTimeEndSet(Date().time + MILLISECONDS_IN_HOUR)
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PrimaryText(
            text = stringResource(id = R.string.date_and_time),
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            OutlinedTextField(
                value = date.toDateWithWeek(),
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
                    value = timeStart.toTime(),
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
                    value = timeEnd.toTime(),
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
            val textTint by animateColorAsState(
                if (isDurationError) Color.Red
                else MaterialTheme.colors.onSurface
            )
            Text(text = "Продолжительность: $duration", color = textTint)
        }
    }
}

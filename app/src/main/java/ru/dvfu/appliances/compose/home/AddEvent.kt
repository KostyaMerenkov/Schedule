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
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import ru.dvfu.appliances.BuildConfig
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.MyCard
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.appliance.FabWithLoading
import ru.dvfu.appliances.compose.components.*
import ru.dvfu.appliances.compose.viewmodels.AddEventViewModel
import ru.dvfu.appliances.compose.components.views.ModalLoadingDialog
import ru.dvfu.appliances.compose.components.views.PrimaryText
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.utils.TimeConstants.FULL_DATE_FORMAT
import ru.dvfu.appliances.model.utils.toHoursAndMinutes
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.ViewState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@Composable
fun AddEvent(selectedDate: LocalDate, upPress: () -> Unit) {
    val today = remember { LocalDate.now() }
    val viewModel: AddEventViewModel = getViewModel(parameters = {
        parametersOf(if (BuildConfig.DEBUG) selectedDate else {
            selectedDate.takeIf { it.isAfter(today) } ?: today
        })
    })
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                upPress()
            }
            else -> {}
        }
    }

    if (uiState is UiState.InProgress) ModalLoadingDialog()

    Scaffold(topBar = {
        ScheduleAppBar(
            title = stringResource(id = R.string.new_event),
            backClick = upPress
        )
    },
        floatingActionButton = {
            FloatingActionButton(onClick = { if (uiState != UiState.Success) viewModel.addEvent() }) {
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
                timeStart = viewModel.timeStart.value.toLocalTime(),
                timeEnd = viewModel.timeEnd.value.toLocalTime(),
                onDateSet = viewModel::onDateSet,
                onTimeStartSet = viewModel::onTimeStartSet,
                onTimeEndSet = viewModel::onTimeEndSet,
                duration = viewModel.duration.collectAsState().value,
                isDurationError = viewModel.isDurationError.collectAsState().value,
            )
            Commentary(
                commentary = viewModel.commentary.value,
                onCommentarySet = viewModel::onCommentarySet
            )
            ChooseAppliance(
                appliancesState = viewModel.appliancesState.collectAsState().value,
                selectedAppliance = viewModel.selectedAppliance.collectAsState(),
                onApplianceSelected = viewModel::onApplianceSelected
            )
        }
    }
}

@Composable
fun Commentary(
    modifier: Modifier = Modifier,
    commentary: String,
    onCommentarySet: (String) -> Unit
) {
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
    date: LocalDate,
    timeStart: LocalTime,
    timeEnd: LocalTime,
    onDateSet: ((LocalDate) -> Unit)? = null,
    onTimeStartSet: ((LocalTime) -> Unit)? = null,
    onTimeEndSet: ((LocalTime) -> Unit)? = null,
    duration: String? = null,
    isDurationError: Boolean = false,
) {
    val context = LocalContext.current

    val dateSetState = remember { mutableStateOf(false) }
    val timeSetStartState = remember { mutableStateOf(false) }
    val timeSetEndState = remember { mutableStateOf(false) }

    onDateSet?.let {
        if (dateSetState.value) DatePicker(context, date, onDateSet = onDateSet) {
            dateSetState.value = false
        }
    }
    onTimeStartSet?.let {
        if (timeSetStartState.value) TimePicker(
            context, timeStart,
            onTimeSet = onTimeStartSet
        ) { timeSetStartState.value = false }
    }
    onTimeEndSet?.let {
        if (timeSetEndState.value) TimePicker(
            context, timeEnd,
            onTimeSet = onTimeEndSet,
        ) { timeSetEndState.value = false }
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PrimaryText(
            text = stringResource(id = R.string.date_and_time),
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            OutlinedTextField(
                value = date.format(FULL_DATE_FORMAT),
                onValueChange = {},
                label = { Text(text = stringResource(R.string.date)) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = {
                    onDateSet?.let {
                        IconButton(onClick = {
                            dateSetState.value = true
                        }) {
                            Icon(
                                Icons.Default.Today,
                                tint = MaterialTheme.colors.primary,
                                contentDescription = stringResource(R.string.date)
                            )
                        }
                    }
                })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
                OutlinedTextField(
                    value = timeStart.toHoursAndMinutes(),
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.time_start)) },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        onTimeStartSet?.let {
                            IconButton(onClick = {
                                timeSetStartState.value = true
                            }) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    tint = MaterialTheme.colors.primary,
                                    contentDescription = stringResource(R.string.time)
                                )
                            }
                        }
                    })
                OutlinedTextField(
                    value = timeEnd.toHoursAndMinutes(),
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.time_end)) },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        onTimeEndSet?.let {
                            IconButton(onClick = {
                                timeSetEndState.value = true
                            }) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    tint = MaterialTheme.colors.primary,
                                    contentDescription = stringResource(R.string.time)
                                )
                            }
                        }
                    })
            }
            val textTint by animateColorAsState(
                if (isDurationError) Color.Red
                else MaterialTheme.colors.onSurface
            )
            duration?.let {
                Text(text = "Продолжительность: $duration", color = textTint)
            }
        }
    }
}

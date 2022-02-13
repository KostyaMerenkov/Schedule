package ru.dvfu.appliances.compose.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import org.koin.androidx.compose.get
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.MyCard
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.appliance.ApplianceDetails
import ru.dvfu.appliances.compose.appliance.ItemUserWithSelection
import ru.dvfu.appliances.compose.components.*
import ru.dvfu.appliances.compose.viewmodels.AddEventViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
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
            ChooseAppliance()
        }
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ChooseAppliance() {
    val currentOption: MutableState<Appliance?> = remember {
        mutableStateOf(null)
    }
    val appliances = mutableListOf<Appliance>()
    appliances.apply {
        repeat(10) {
            add(Appliance(name = it.toString()))
        }
    }
    ApplianceSelection(radioOptions = appliances, currentOption = currentOption, onSelectedItem = {
        currentOption.value = it
    })
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemApplianceSelectable(appliance: Appliance, isSelected: Boolean, applianceClicked: () -> Unit) {
    val border = animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colors.primary else Color.White.copy(0f)
    )
    val selectedColor = animateColorAsState(
        targetValue = if (isSelected) Color.LightGray else MaterialTheme.colors.surface
    )
    val borderModifier = if (isSelected) Modifier else Modifier
    MyCard(
        onClick = applianceClicked, modifier = Modifier/*.border(2.dp, border.value)*/,
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
                    .border(1.dp, Color.Black, CircleShape)
                    .background(Color(appliance.color)),
            ) {
                Text(
                    if (appliance.name.isEmpty()) ""
                    else appliance.name.first().uppercase(),
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h4,
                )
            }
            Text(appliance.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            /*Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircleOutline,
                        "",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }*/
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
    }
}

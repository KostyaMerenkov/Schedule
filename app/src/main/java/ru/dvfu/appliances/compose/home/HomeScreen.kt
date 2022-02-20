package ru.dvfu.appliances.compose.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.components.FabMenuItem
import ru.dvfu.appliances.compose.components.FabWithMenu
import ru.dvfu.appliances.compose.components.MultiFabState
import ru.dvfu.appliances.compose.event_calendar.Schedule
import ru.dvfu.appliances.compose.event_calendar.sampleEvents
import java.time.LocalDate

@Composable
fun MainScreen(navController: NavController, openDrawer: () -> Unit) {
    val viewModel: MainScreenViewModel = getViewModel()
    val events by viewModel.events.collectAsState()

    val fabState = remember { mutableStateOf(MultiFabState.COLLAPSED) }

    Scaffold(topBar = {
        TopAppBar(
            title = { /*Text(text = R.string.androidx_startup)*/ },
            navigationIcon = {
                IconButton(onClick = { openDrawer() }
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = "")
                }
            },
            backgroundColor = Color(0xFFFF5470)
        )
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
                    onClick = { navController.navigate(MainDestinations.ADD_EVENT) }
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
        /*Schedule(sampleEvents,
            //minDate = LocalDate.now().minusDays(1),
            //maxDate = LocalDate.now().plusDays(1)
        )*/
        Schedule(events = events, minDate = LocalDate.now().minusDays(1),
        maxDate = LocalDate.now().plusDays(1))
    }
}
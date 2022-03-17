package ru.dvfu.appliances.compose.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import ru.dvfu.appliances.compose.ItemAppliance
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.UserInfo
import ru.dvfu.appliances.compose.viewmodels.EventInfoViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.ViewState

@Composable
fun EventInfo(event: Event, backPress: () -> Unit) {

    val viewModel: EventInfoViewModel = getViewModel(parameters = { parametersOf(event.applianceId) })
    val applianceState by viewModel.applianceState.collectAsState()
    Scaffold(
        topBar = { EventInfoTopBar(backPress) {

        } }
    ) {
        Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
            EventAppliance(applianceState) {  }
            EventUser()
        }
    }


}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun EventAppliance(applianceState: ViewState<Appliance>, applianceClicked: (Appliance) -> Unit) {
    Crossfade(targetState = applianceState) {
        when(it) {
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

@OptIn(ExperimentalCoilApi::class)
@Composable
fun EventUser() {
    UserInfo(user = User(email = "email@abc.com", userName = "Name"))
}

@Preview(
    showBackground = true,
    //showSystemUi = true
)
@Composable
fun EventInfoPreview() {
    EventInfo(Event(), { })
}
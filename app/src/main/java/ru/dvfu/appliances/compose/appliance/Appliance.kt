package ru.dvfu.appliances.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.viewmodels.ApplianceViewModel

@Composable
fun Appliance(navController: NavController, upPress: () -> Unit, appliance: Appliance) {

    val viewModel: ApplianceViewModel = get()
    viewModel.appliance.value = appliance
    var infoDialogState = remember { mutableStateOf(false) }

    val user: User by viewModel.user.collectAsState(User())

    if (infoDialogState.value) ApplianceInfoDialog(infoDialogState, appliance)

    Scaffold(topBar = {
        ApplianceTopBar(user, appliance, viewModel, upPress)
    }) {
        Column() {
            Surface(
                modifier = Modifier.fillMaxWidth().requiredHeightIn(min = 60.dp, max = 120.dp),
                color = MaterialTheme.colors.primary
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentHeight()
                ) {
                    if (appliance.description.isNotEmpty()) {
                        IconButton(onClick = { infoDialogState.value = true }) {
                            Icon(Icons.Default.Info, "")
                        }
                    }
                    Text(
                        appliance.name,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.h4,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

        }

    }
}

@Composable
fun ApplianceTopBar(
    user: User,
    appliance: Appliance,
    viewModel: ApplianceViewModel,
    upPress: () -> Unit
) {
    if (permitToDeleteAppliance(user, appliance)) {
        ScheduleAppBar(
            backClick = upPress,
            actionDelete = true,
            deleteClick = { viewModel.deleteAppliance(); upPress() },
            elevation = 0.dp
        )
    } else {
        ScheduleAppBar(
            backClick = upPress,
            elevation = 0.dp
        )
    }

}

fun permitToDeleteAppliance(user: User, appliance: Appliance) = appliance.superuserIds.contains(user.userId)

@Composable
fun ApplianceInfoDialog(infoDialogState: MutableState<Boolean>, appliance: Appliance) {
    Dialog(onDismissRequest = { infoDialogState.value = false }) {
        Card(
            modifier = Modifier.padding(horizontal = 24.dp).wrapContentHeight(),
            shape = RoundedCornerShape(25.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    appliance.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6
                )
                Text(appliance.description)
            }
        }
    }
}

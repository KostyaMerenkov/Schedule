package ru.dvfu.appliances.compose

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.viewmodels.ApplianceViewModel

@Composable
fun Appliance(navController: NavController, upPress: () -> Unit, appliance: Appliance) {

    val viewModel: ApplianceViewModel = get()
    viewModel.appliance.value = appliance

    Scaffold(topBar = {
        ScheduleAppBar(appliance.name, upPress, deleteClick = { viewModel.deleteAppliance() })
    }) {
        Text("Appliance = " + appliance.name)
    }
}
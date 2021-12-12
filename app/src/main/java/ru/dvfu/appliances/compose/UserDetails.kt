package ru.dvfu.appliances.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.compose.appliance.LoadingItem
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.compose.viewmodels.UserDetailsViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Role

@Composable
fun UserDetails(navController: NavController, upPress: () -> Unit, user: User) {

    val viewModel: UserDetailsViewModel = get()
    viewModel.setUser(user)
    //viewModel.getAppliances(user)

    Scaffold(topBar = {
        ScheduleAppBar(user.email, upPress)
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("User: " + user.userName)
            Text("Role: " + Role.values()[user.role].name)

            when(user.role) {
                Role.ADMIN.ordinal -> {}
                Role.USER.ordinal -> { UserAppliancesList(viewModel, navController) }
                Role.GUEST.ordinal -> {}
                Role.SUPERUSER.ordinal -> { SuperUserAppliancesList(viewModel, navController) }
            }
        }

    }
}

@Composable
fun SuperUserAppliancesList(viewModel: UserDetailsViewModel, navController: NavController) {
    val appliances by viewModel.currentSuperUserAppliances.collectAsState()
    UserAppliancesList(appliances, navController)
}

@Composable
fun UserAppliancesList(viewModel: UserDetailsViewModel, navController: NavController) {
    val appliances by viewModel.currentUserAppliances.collectAsState()
    UserAppliancesList(appliances, navController)
}


@OptIn(ExperimentalFoundationApi::class ,ExperimentalAnimationApi::class)
@Composable
fun UserAppliancesList(appliances: List<Appliance>?, navController: NavController) {
    appliances?.let {
        LazyRow {
            items(appliances) { item ->
                ItemAppliance(item, applianceClicked = { navController.navigate(
                    MainDestinations.APPLIANCE_ROUTE,
                    Arguments.APPLIANCE to it) })
            }
        }
    } ?: LoadingItem()

}
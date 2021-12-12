package ru.dvfu.appliances.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val updatedUser by viewModel.currentUser.collectAsState()
    //viewModel.getAppliances(user)

    Scaffold(topBar = {
        ScheduleAppBar(user.email, upPress)
    }) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState(0))) {
            UserNameAndRole(updatedUser)


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
fun UserNameAndRole(user: User) {
    OutlinedTextField(value = user.userName, onValueChange = {}, readOnly = true,
        label = {Text("Name")})
    OutlinedTextField(value = Role.values()[user.role].name, onValueChange = {}, readOnly = true,
        label = {Text("Role")})
}


@Composable
fun SuperUserAppliancesList(viewModel: UserDetailsViewModel, navController: NavController) {
    val userAppliances by viewModel.currentUserAppliances.collectAsState()
    val superUserAppliances by viewModel.currentSuperUserAppliances.collectAsState()
    UserAppliancesList(viewModel, navController)
    Row(modifier = Modifier.padding(10.dp)) {
        Text(text = "Приборы суперпользователя")
    }
    AppliancesList(superUserAppliances, navController)
}

@Composable
fun UserAppliancesList(viewModel: UserDetailsViewModel, navController: NavController) {
    val appliances by viewModel.currentUserAppliances.collectAsState()
    Row(modifier = Modifier.padding(10.dp)) {
        Text(text = "Приборы пользователя")
    }
    AppliancesList(appliances, navController)
}


@OptIn(ExperimentalFoundationApi::class ,ExperimentalAnimationApi::class)
@Composable
fun AppliancesList(appliances: List<Appliance>?, navController: NavController) {
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
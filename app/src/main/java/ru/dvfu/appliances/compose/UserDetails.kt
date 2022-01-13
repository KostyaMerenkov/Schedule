package ru.dvfu.appliances.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.appliance.LoadingItem
import ru.dvfu.appliances.compose.viewmodels.UserDetailsViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.getRole

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
            UserNameAndRole(updatedUser,) {
                //isChangeRoleDialogOpen = true
            }


            when (user.role) {
                Roles.ADMIN.ordinal -> {}
                Roles.USER.ordinal -> {
                    UserAppliancesList(viewModel, navController)
                }
                Roles.GUEST.ordinal -> {}
                Roles.SUPERUSER.ordinal -> {
                    SuperUserAppliancesList(viewModel, navController)
                }
            }
        }

    }
}

@Composable
fun UserNameAndRole(user: User, onRoleChangeClick: () -> Unit) {
    HeaderText(text = stringResource(R.string.user_info))
    OutlinedTextField(value = user.userName, onValueChange = {}, readOnly = true,
        label = { Text(stringResource(R.string.name)) })
    OutlinedTextField(value = stringResource(getRole(user.role).stringRes), onValueChange = {}, readOnly = true,
        label = { Text(stringResource(R.string.role)) }, trailingIcon = {
            if (getRole(user.role).isAdmin()) {
                IconButton(onClick = onRoleChangeClick) {
                    Icon(Icons.Default.Edit, Icons.Default.Edit.name)
                }
            }
        })
}


@Composable
fun SuperUserAppliancesList(viewModel: UserDetailsViewModel, navController: NavController) {
    val superUserAppliances by viewModel.currentSuperUserAppliances.collectAsState()
    UserAppliancesList(viewModel, navController)
    HeaderText(text = stringResource(R.string.superuser_appliances))
    AppliancesList(superUserAppliances, navController)
}

@Composable
fun UserAppliancesList(viewModel: UserDetailsViewModel, navController: NavController) {
    val appliances by viewModel.currentUserAppliances.collectAsState()
    HeaderText(text = stringResource(R.string.user_appliances))
    AppliancesList(appliances, navController)
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun AppliancesList(appliances: List<Appliance>?, navController: NavController) {
    appliances?.let {
        LazyRow {
            if (appliances.isNotEmpty()) {
                items(appliances) { item ->
                    ItemAppliance(item, applianceClicked = {
                        navController.navigate(
                            MainDestinations.APPLIANCE_ROUTE,
                            Arguments.APPLIANCE to it
                        )
                    })
                }
            } else {
                item {
                    NoAppliances()
                }
            }

        }
    } ?: LoadingItem()
}

@Composable
fun NoAppliances() {
    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(R.string.no_appliances))
    }
}

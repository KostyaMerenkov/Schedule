package ru.dvfu.appliances.compose

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.appliance.LoadingItem
import ru.dvfu.appliances.compose.components.ItemsSelection
import ru.dvfu.appliances.compose.viewmodels.UserDetailsViewModel
import ru.dvfu.appliances.compose.views.HeaderText
import ru.dvfu.appliances.compose.views.PrimaryText
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.getRole

@ExperimentalComposeUiApi
@Composable
fun UserDetails(navController: NavController, upPress: () -> Unit, user: User) {

    val viewModel: UserDetailsViewModel = get()

    DisposableEffect(viewModel) {
        viewModel.setDetailsUser(user)
        onDispose {}
    }

    var isChangeRoleDialogOpen by remember { mutableStateOf(false) }
    val currentUser by viewModel.currentUser.collectAsState()
    val detailsUser by viewModel.detailsUser.collectAsState()

    //viewModel.getAppliances(user)


    Scaffold(topBar = {
        ScheduleAppBar(stringResource(R.string.user), upPress)
    }) {

        if (isChangeRoleDialogOpen) RolesWithSelectionDialog(currentUser = detailsUser,
            onDismiss = { isChangeRoleDialogOpen = false }) { newRole ->
            viewModel.updateUserRole(detailsUser, newRole.ordinal)
        }

        Column(modifier = Modifier
            .fillMaxSize().padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState(0))) {
            UserInfo(detailsUser, currentUser) {
                isChangeRoleDialogOpen = true
            }

            when (detailsUser.role) {
                Roles.GUEST.ordinal -> {}
                Roles.USER.ordinal, Roles.ADMIN.ordinal -> {
                    //UserAppliancesList(viewModel, navController)
                    SuperUserAppliancesList(viewModel, navController)
                }
            }
        }
    }
}

@Composable
fun RolesWithSelectionDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    onSelectedValue: (Roles) -> Unit
) {
    val radioOptions = Roles.values().asList()
    val context = LocalContext.current
    val currentUserRole = remember {
        mutableStateOf(getRole(currentUser.role))
    }
    Dialog(onDismissRequest = onDismiss) {
        ItemsSelection(Modifier, radioOptions, currentUserRole) { onSelectedValue(it) }
    }
}


@Composable
fun UserInfo(user: User, currentUser: User, onRoleChangeClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()) {

        HeaderText(text = stringResource(R.string.user_info))
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = user.userName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.name)) })
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = user.email,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.email)) })
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = stringResource(getRole(user.role).stringRes),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.role)) },
            trailingIcon = {
                if (getRole(currentUser.role).isAdmin() && user.userId != currentUser.userId) {
                    IconButton(onClick = onRoleChangeClick) {
                        Icon(Icons.Default.Edit, Icons.Default.Edit.name)
                    }
                }
            })
    }
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
        LazyRow(contentPadding = PaddingValues(10.dp)) {
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

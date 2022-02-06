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

        if (isChangeRoleDialogOpen) RolesWithSelectionDialog(currentUser = user,
            onDismiss = { isChangeRoleDialogOpen = false }) { newRole ->
            viewModel.updateUserRole(user, newRole.ordinal)
        }

        Column(modifier = Modifier
            .fillMaxSize().padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState(0))) {
            UserInfo(detailsUser, currentUser) {
                isChangeRoleDialogOpen = true
            }

            when (user.role) {
                Roles.USER.ordinal -> {
                    UserAppliancesList(viewModel, navController)
                }
                Roles.GUEST.ordinal -> {}
                Roles.SUPERUSER.ordinal, Roles.ADMIN.ordinal -> {
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
    val currentUserRole = getRole(currentUser.role)

    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            currentUserRole
        )
    }
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PrimaryText(
                        text = String.format(
                            "Выберите новую роль для пользователя \n\"%s\"", currentUser.userName
                        )
                    )
                }

                radioOptions.forEach { role ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .selectable(
                                selected = (currentUserRole == role),
                                onClick = {
                                    onOptionSelected(role)
                                    if (currentUser.role != role.ordinal) onSelectedValue(role)
                                }
                            )
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = (currentUserRole == role),
                            modifier = Modifier.padding(all = Dp(value = 8F)),
                            onClick = {
                                onOptionSelected(role)
                                if (currentUser.role != role.ordinal) onSelectedValue(role)
                                Toast.makeText(
                                    context,
                                    role.name,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                        Text(
                            text = stringResource(role.stringRes),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }

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
                if (getRole(currentUser.role).isAdmin()) {
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

package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import org.koin.androidx.compose.viewModel
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.viewmodels.ApplianceUsersViewModel
import ru.dvfu.appliances.compose.viewmodels.ApplianceViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ApplianceSuperUsers(
    navController: NavController,
    appliance: Appliance,
) {
    val viewModel: ApplianceViewModel by viewModel()

    val superUsers by viewModel.currentSuperUsers.collectAsState()

    Scaffold(backgroundColor = Color.Transparent) {

        Crossfade(superUsers) { animatedUiState ->
            Users(
                users = animatedUiState,
                userClicked = { superUsers ->
                    /*onUserClick(user, navController)*/
                },
                addClicked = { onAddSuperUserClick(navController, appliance) },
                deleteClicked = { userToDelete ->
                    viewModel.deleteSuperUser(userToDelete, appliance)
                }
            )
        }
    }
}

fun onAddSuperUserClick(navController: NavController, appliance: Appliance) {
    navController.navigate(
        MainDestinations.ADD_SUPERUSER_TO_APPLIANCE,
        Arguments.APPLIANCE to appliance)
}



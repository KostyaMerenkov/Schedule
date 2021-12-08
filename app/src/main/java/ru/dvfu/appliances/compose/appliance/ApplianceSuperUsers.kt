package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.viewmodels.ApplianceSuperUsersViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User

@ExperimentalAnimationApi
@Composable
fun ApplianceSuperUsers(
    navController: NavController,
    appliance: Appliance,
) {
    val viewModel: ApplianceSuperUsersViewModel = get()
    viewModel.loadAllSuperUsers(appliance)

    val users: List<User> by viewModel.currentContent.collectAsState()

    Scaffold(backgroundColor = Color.Transparent) {

        Crossfade(users) { animatedUiState ->
            Users(
                users = animatedUiState,
                userClicked = { user ->
                    /*onUserClick(user, navController)*/
                },
                addClicked = { onAddSuperUserClick(navController, appliance) }
            )
        }
    }
}

fun onAddSuperUserClick(navController: NavController, appliance: Appliance) {
    navController.navigate(
        MainDestinations.ADD_SUPERUSER_TO_APPLIANCE,
        Arguments.APPLIANCE to appliance)
}



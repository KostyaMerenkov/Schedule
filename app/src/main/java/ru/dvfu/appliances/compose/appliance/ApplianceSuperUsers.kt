package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import org.koin.androidx.compose.viewModel
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.navigate
import ru.dvfu.appliances.compose.viewmodels.ApplianceDetailsViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ApplianceSuperUsers(
    navController: NavController,
    appliance: Appliance,
) {
    val detailsViewModel: ApplianceDetailsViewModel by viewModel()
    val currentUser by detailsViewModel.currentUser.collectAsState(User())

    val superUsers by detailsViewModel.currentSuperUsers.collectAsState()

    Scaffold(backgroundColor = Color.Transparent) {

        superUsers?.let {
            Crossfade(it) { animatedUiState ->
                SwipableUsers(
                    users = animatedUiState,
                    userClicked = { superUser ->
                        onSuperUserClick(superUser, navController)
                    },
                    addClicked = { onAddSuperUserClick(navController, appliance) },
                    deleteClicked = { userToDelete ->
                        detailsViewModel.deleteSuperUser(userToDelete, appliance)
                    },
                    isSuperuserOrAdmin = appliance.isUserSuperuserOrAdmin(currentUser)
                )
            }
        } ?: AnimatedVisibility(visible = superUsers == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) { CircularProgressIndicator() }
        }


    }
}

private fun onSuperUserClick(user: User, navController: NavController) {
    navController.navigate(
        MainDestinations.USER_DETAILS_ROUTE,
        Arguments.USER to user
    )
}

fun onAddSuperUserClick(navController: NavController, appliance: Appliance) {
    navController.navigate(
        MainDestinations.ADD_SUPERUSER_TO_APPLIANCE,
        Arguments.APPLIANCE to appliance)
}



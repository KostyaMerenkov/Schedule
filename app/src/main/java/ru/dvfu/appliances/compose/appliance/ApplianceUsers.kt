package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.compose.Arguments
import ru.dvfu.appliances.compose.MainDestinations
import ru.dvfu.appliances.compose.viewmodels.ApplianceUsersViewModel
import ru.dvfu.appliances.model.repository.entity.User

@ExperimentalAnimationApi
@Composable
fun ApplianceUsers(
    navController: NavController,
    viewModel: ApplianceUsersViewModel = get()
) {
    Scaffold(backgroundColor = Color.Transparent) {
        val users: List<User> by viewModel.currentContent.collectAsState()
        /*Crossfade(users) { animatedUiState ->
            Users(
                users = animatedUiState,
                userClicked = { user ->
                    onUserClick(user, navController)
                }
            )
        }*/
    }
}

@ExperimentalAnimationApi
@Composable
fun Users(
    users: List<User>,
    userClicked: (User) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        when {
            users.isNotEmpty() -> {
                items(users) { user ->
                    ItemUser(
                        users = user
                    ) { userClicked(user) }
                }
            }
            places.isEmpty() -> {
                item {
                    NoElementsView(
                        mainText = stringResource(R.string.no_places_added),
                        secondaryText = stringResource(R.string.new_place_text),
                        onClickAction = { }
                    )
                }

            }
        }

    }
}

private fun onUserClick(user: User, navController: NavController) {
    /*navController.navigate(
        MainDestinations.USER_ROUTE,
        Arguments.USER to user
    )*/
}
package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.compose.ItemAdd
import ru.dvfu.appliances.compose.ItemUser
import ru.dvfu.appliances.compose.viewmodels.ApplianceUsersViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User

@ExperimentalAnimationApi
@Composable
fun ApplianceUsers(
    navController: NavController,
    appliance: Appliance,
) {
    val viewModel: ApplianceUsersViewModel = get()
    viewModel.loadAllUsers(appliance)

    val users: List<User> by viewModel.currentContent.collectAsState()


    Scaffold(backgroundColor = Color.Transparent) {

        Crossfade(users) { animatedUiState ->
            Users(
                users = animatedUiState,
                userClicked = { user ->
                    onUserClick(user, navController)
                },
                addClicked = {}
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@Composable
fun Users(
    users: List<User>,
    userClicked: (User) -> Unit,
    addClicked: () -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {

        item { ItemAdd(addClicked) }
        when {
            users.isNotEmpty() -> {
                items(users) { user ->
                    ItemUser(user) { userClicked(user) }
                }
            }
            users.isEmpty() -> {
                /*item {
                    NoElementsView(
                        mainText = stringResource(R.string.no_places_added),
                        secondaryText = stringResource(R.string.new_place_text),
                        onClickAction = { }
                    )
                }*/
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
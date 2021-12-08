package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.*
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
                    addClicked = { onAddClick(navController, appliance) }
            )
        }
    }
}

fun onAddClick(navController: NavController, appliance: Appliance) {
    navController.navigate(MainDestinations.ADD_USER_TO_APPLIANCE,
        Arguments.APPLIANCE to appliance)
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
        if (users.isNotEmpty()) {
            items(users) { user ->
                ItemUser(user) { userClicked(user) }
            }
        } else
            item {
                NoElementsView(
                        mainText = stringResource(R.string.no_users_in_appliance),
                        //secondaryText = stringResource(R.string.new_place_text),
                        onClickAction = { }
                )
            }
    }

}


@Composable
fun NoElementsView(
        modifier: Modifier = Modifier,
        mainText: String,
        secondaryText: String = "",
        onClickAction: () -> Unit
) {
    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp)
    ) {
        Text(text = mainText)
        Spacer(modifier = Modifier.height(8.dp))
        if (secondaryText.isNotEmpty()) {
            Text(
                    modifier = Modifier.clickable {
                        onClickAction()
                    },
                    text = secondaryText
            )
        }
    }
}

private fun onUserClick(user: User, navController: NavController) {
    /*navController.navigate(
        MainDestinations.USER_ROUTE,
        Arguments.USER to user
    )*/
}
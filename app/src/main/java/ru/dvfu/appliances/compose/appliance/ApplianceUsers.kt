package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import org.koin.androidx.compose.viewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.*
import ru.dvfu.appliances.compose.viewmodels.ApplianceDetailsViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User


@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ApplianceUsers(
    navController: NavController,
    appliance: Appliance,
) {
    val detailsViewModel: ApplianceDetailsViewModel by viewModel()
    val currentUser: User by detailsViewModel.currentUser.collectAsState(User())

    val users by detailsViewModel.currentUsers.collectAsState()

    Scaffold(backgroundColor = Color.Transparent) {

        Crossfade(users) { animatedUiState ->
            SwipableUsers(
                users = animatedUiState,
                userClicked = { user ->
                    onUserClick(user, navController)
                },
                addClicked = { onAddClick(navController, appliance) },
                deleteClicked = { userToDelete ->
                    detailsViewModel.deleteUser(userToDelete, appliance)
                },
                isSuperuserOrAdmin = appliance.isUserSuperuserOrAdmin(currentUser)
            )
        }
    }
}

fun onAddClick(navController: NavController, appliance: Appliance) {
    navController.navigate(
        MainDestinations.ADD_USER_TO_APPLIANCE,
        Arguments.APPLIANCE to appliance
    )
}

@ExperimentalMaterialApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@Composable
fun SwipableUsers(
    users: List<User>?,
    userClicked: (User) -> Unit,
    addClicked: () -> Unit,
    deleteClicked: (User) -> Unit,
    isSuperuserOrAdmin: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp)
    ) {
        if (isSuperuserOrAdmin) item { ItemAdd(addClicked) }
        users?.let {
            if (users.isNotEmpty()) {
                items(users) { user ->
                    if (isSuperuserOrAdmin) {
                        ItemSwipableUser(user,
                            userClicked = { userClicked(user) },
                            userDeleted = { deleteClicked(user) })
                    } else {
                        ItemUser(
                            user,
                            userClicked = { userClicked(user) },
                        )
                    }
                }
            } else
                item {
                    NoElementsView(
                        mainText = stringResource(R.string.no_users_in_appliance),
                        //secondaryText = stringResource(R.string.new_place_text),
                        onClickAction = { }
                    )
                }
        } ?: item {
            LoadingItem(Modifier.fillMaxSize())
        }
    }
}

@Composable
fun LoadingItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
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
            .fillMaxSize()
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

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemSwipableUser(user: User, userClicked: () -> Unit, userDeleted: () -> Unit) {

    RevealSwipe(
        //modifier = Modifier.padding(vertical = 5.dp),
        directions = setOf(
            //RevealDirection.StartToEnd,
            RevealDirection.EndToStart
        ),
        /*hiddenContentStart = {
            Icon(
                modifier = Modifier.padding(horizontal = 25.dp),
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = Color.White
            )
        },*/
        hiddenContentEnd = {
            Icon(
                modifier = Modifier.padding(horizontal = 25.dp),
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        onBackgroundEndClick = userDeleted
    ) {
        ItemUser(user, userClicked)
    }
}

private fun onUserClick(user: User, navController: NavController) {
    navController.navigate(
        MainDestinations.USER_DETAILS_ROUTE,
        Arguments.USER to user
    )
}
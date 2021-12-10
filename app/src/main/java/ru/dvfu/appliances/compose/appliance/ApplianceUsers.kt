package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import org.koin.androidx.compose.viewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.*
import ru.dvfu.appliances.compose.viewmodels.ApplianceViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ApplianceUsers(
    navController: NavController,
    appliance: Appliance,
) {
    val viewModel: ApplianceViewModel by viewModel()

    val users by viewModel.currentUsers.collectAsState()

    Scaffold(backgroundColor = Color.Transparent) {

        Crossfade(users) { animatedUiState ->
            SwipableUsers(
                users = animatedUiState,
                userClicked = { user ->
                    onUserClick(user, navController)
                },
                addClicked = { onAddClick(navController, appliance) },
                deleteClicked = { userToDelete ->
                    viewModel.deleteUser(userToDelete, appliance)
                }
            )
        }
        /*?: AnimatedVisibility(visible = users == null) {
           Box(
               modifier = Modifier.fillMaxSize(),
               contentAlignment = Alignment.TopCenter,
           ) { CircularProgressIndicator() }
       }*/
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
    deleteClicked: (User) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { ItemAdd(addClicked) }
        users?.let {
            if (users.isNotEmpty()) {
                items(users) { user ->
                    ItemSwipableUser(user,
                        userClicked = { userClicked(user) },
                        userDeleted = { deleteClicked(user) })
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

/*@ExperimentalMaterialApi
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@Composable
fun Users(
    users: List<User>?,
    userClicked: (User) -> Unit,
    addClicked: () -> Unit,
    deleteClicked: (User) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { ItemAdd(addClicked) }
        users?.let {
            if (users.isNotEmpty()) {
                items(users) { user ->
                    ItemUser(user,
                        userClicked = { userClicked(user) },
                        userDeleted = { deleteClicked(user) })
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}*/


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
        modifier = Modifier.padding(vertical = 5.dp),
        directions = setOf(
            RevealDirection.StartToEnd,
            RevealDirection.EndToStart
        ),
        hiddenContentStart = {
            Icon(
                modifier = Modifier.padding(horizontal = 25.dp),
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = Color.White
            )
        },
        hiddenContentEnd = {
            Icon(
                modifier = Modifier.padding(horizontal = 25.dp),
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        onBackgroundEndClick = userDeleted
    ) {
        MyCard( modifier = Modifier
            .requiredHeight(80.dp)
            .fillMaxWidth().padding(horizontal = 10.dp),
            onClick = userClicked) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                if (user.userPic.isNullOrEmpty()) {
                    Icon(
//                        painter = rememberImagePainter(photo),
                        painterResource(R.drawable.ic_guest),
                        stringResource(R.string.No),
                        modifier = Modifier.clip(CircleShape)
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically),
                        //tint = secondaryFigmaColor
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(user.userPic,
                            builder = {
                                crossfade(true)
                                placeholder(R.drawable.ic_launcher_foreground)
                                transformations(CircleCropTransformation())
                            }),
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically),
                        contentDescription = stringResource(R.string.user_photo),
                        //contentScale = ContentScale.Crop,


                    )
                }
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(user.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(user.email)
                }
            }
        }
    }

}

private fun onUserClick(user: User, navController: NavController) {
    navController.navigate(
        MainDestinations.USER_DETAILS_ROUTE,
        Arguments.USER to user
    )
}
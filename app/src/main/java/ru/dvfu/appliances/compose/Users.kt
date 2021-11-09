package ru.dvfu.appliances.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.viewmodels.UsersViewModel
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.Role

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun Users(navController: NavController, backPress: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = getViewModel<UsersViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val refreshing by remember { viewModel.isRefreshing }
    //val isRefreshing by viewModel.isRefreshing.collectAsState()

    /*LaunchedEffect(refreshing) {
        if (refreshing) {

            delay(2000)
            refreshing = false
        }
    }*/

    val users by viewModel.usersList.collectAsState()
    Scaffold(topBar = { ScheduleAppBar(stringResource(R.string.users), backClick = backPress) }) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(refreshing),
            onRefresh = { viewModel.refresh() },
            Modifier
                .background(Color(0XFFE3DAC9))
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { Spacer(modifier.size(4.dp)) }
                val myUsers = users
                for (role in Role.values().reversed()) {
                    val role_users = mutableListOf<User>()
                    myUsers.forEach { user ->
                        if (user.role == role.ordinal) role_users.add(user)
                    }
                    if (role_users.isNotEmpty()) stickyHeader { Header(role.name) }
                    items(role_users) { user ->
                        ItemUser(
                            user = user,
                            userClicked = {
                                navController.navigate(
                                    MainDestinations.USER_DETAILS_ROUTE,
                                    Arguments.USER to user
                                )
                            }
                        )
                    }
                }
            }

            /*Crossfade(uiState, animationSpec = tween(500)) { animatedUiState ->
                when (animatedUiState) {
                    is BaseViewState.Loading ->
                        UserCatchesLoading { onAddNewCatchClick(navController) }
                    is BaseViewState.Success<*> -> UserCatches(
                        (uiState as BaseViewState.Success<*>).data as List<UserCatch>,
                        { onAddNewCatchClick(navController) }, { catch -> onCatchItemClick(catch, navController) })
                    is BaseViewState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "An error occurred fetching the catches.")
                        }
                    }
                }
            }*/
        }

    }
}

@Composable
fun Header(role: String) {
    Text(role, modifier = Modifier.padding(2.dp).padding(start = 6.dp))
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemUser(user: User, userClicked: () -> Unit) {
    MyCard(onClick = userClicked) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .requiredHeight(80.dp)
                .fillMaxWidth()
                .padding(10.dp)
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

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemAdd(addClicked: () -> Unit) {
    MyCard(onClick = addClicked) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .requiredHeight(80.dp)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Добавить нового пользователя", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

        }

    }
}
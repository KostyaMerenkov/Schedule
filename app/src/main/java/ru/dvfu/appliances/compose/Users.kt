package ru.dvfu.appliances.compose

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.viewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.appliance.ItemUserWithSelection
import ru.dvfu.appliances.compose.components.FullscreenLoading
import ru.dvfu.appliances.compose.viewmodels.UsersViewModel
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.ui.ViewState

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun Users(navController: NavController, backPress: () -> Unit) {

    val viewModel: UsersViewModel = getViewModel()
    val usersState by viewModel.userState.collectAsState()

    Scaffold(
        backgroundColor = Color(0XFFE3DAC9),
        topBar = { ScheduleAppBar(stringResource(R.string.users), backClick = backPress) },
    ) {
        Crossfade(usersState) {
            when (it) {
                is ViewState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        item { Spacer(Modifier.size(4.dp)) }
                        it.data.groupBy { it.role }.forEach { (role, users) ->
                            stickyHeader { Header(stringResource(Roles.values()[role].stringRess)) }
                            items(users) { user ->
                                ItemUser(
                                    user = user,
                                    userClicked = {
                                        navController.navigate(
                                            MainDestinations.USER_DETAILS_ROUTE,
                                            Arguments.USER to user
                                        )
                                    },
                                )
                            }
                        }

                    }
                }
                is ViewState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "An error occurred")
                    }
                }
                is ViewState.Loading -> {
                    FullscreenLoading()
                }
            }
        }
    }
}

@Composable
fun Header(role: String) {
    Text(
        role, modifier = Modifier
            .padding(2.dp)
            .padding(horizontal = 10.dp), style = MaterialTheme.typography.h6
    )
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemUser(user: User, userClicked: () -> Unit) {
    ItemUserWithSelection(user, false, userClicked)
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemAdd(addClicked: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()

    ) {

        MyCard(
            modifier = Modifier
                .requiredHeight(80.dp)
                .clip(CircleShape)
                .padding(15.dp),
            onClick = addClicked
        ) {

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(Icons.Default.PersonAdd, Icons.Default.PersonAdd.name)
            }
        }
    }
}


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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
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
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.appliance.ItemUserWithSelection
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
                users.groupBy { it.role }.forEach { (role, users) ->
                    stickyHeader { Header(Role.values()[role].name) }
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
    Text("$role's", modifier = Modifier.padding(2.dp).padding(horizontal = 10.dp), style = MaterialTheme.typography.h6)
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

    MyCard( modifier = Modifier
        .requiredHeight(80.dp).clip(CircleShape).padding(10.dp),
        onClick = addClicked) {

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Default.PersonAdd, Icons.Default.PersonAdd.name)
            }
        }
    }
}
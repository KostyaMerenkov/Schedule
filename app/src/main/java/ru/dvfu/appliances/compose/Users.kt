package ru.dvfu.appliances.compose

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.viewmodels.ProfileViewModel
import ru.dvfu.appliances.compose.viewmodels.UsersViewModel
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.BaseViewState

@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun Users(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel = getViewModel<UsersViewModel>()

        val uiState by viewModel.uiState.collectAsState()

        val isRefreshing by viewModel.isRefreshing.collectAsState()
        val users by viewModel.usersList.collectAsState()
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                items(users) {
                    ItemUser(
                        user = it,
                        userClicked = {/*TODO*/ }
                    )
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

@ExperimentalAnimationApi
@Composable
fun ItemUser(user: User, userClicked: (User) -> Unit) {
    Card(modifier = Modifier.padding(5.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(75.dp)
                .fillMaxWidth()
                .clickable { userClicked(user) }
                .padding(5.dp)
        ) {
            if (user.userPic.isNullOrEmpty()) {
                Icon(
//                        painter = rememberImagePainter(photo),
                    painterResource(R.drawable.ic_guest),
                    stringResource(R.string.No),
                    modifier = Modifier
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
                        }),
                    stringResource(R.string.user_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
            ) {
                Text(user.userName.split(" ")[0], fontWeight = FontWeight.Bold)
            }
        }
    }
}
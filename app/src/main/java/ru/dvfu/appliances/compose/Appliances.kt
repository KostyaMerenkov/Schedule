package ru.dvfu.appliances.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.viewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.viewmodels.AppliancesViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.ViewState

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun Appliances(navController: NavController, backPress: () -> Unit, modifier: Modifier = Modifier) {

    val viewModel: AppliancesViewModel by viewModel()

    val uiState by viewModel.uiState.collectAsState()
    val refreshing by remember { viewModel.isRefreshing }

    val user: User by viewModel.user.collectAsState(User())
    //val isRefreshing by viewModel.isRefreshing.collectAsState()

    /*LaunchedEffect(refreshing) {
        if (refreshing) {

            delay(2000)
            refreshing = false
        }
    }*/

    val appliances by viewModel.appliancesList.collectAsState()
    Scaffold(
        topBar = { if (user.role >= Roles.ADMIN.ordinal) ScheduleAppBar(stringResource(R.string.appliances), backClick = backPress,
            actionAdd = true, addClick = { navController.navigate(MainDestinations.NEW_APPLIANCE_ROUTE) })
                 else ScheduleAppBar(stringResource(R.string.appliances), backClick = backPress, ) },
        //floatingActionButton = { if (user.role >= Role.ADMIN.ordinal) AppliancesFab(navController) },
        modifier = Modifier.fillMaxSize(),
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(refreshing),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0XFFE3DAC9)),
        ) {
            when (uiState) {
                is ViewState.Error -> {}
                is ViewState.Loading -> {}
                is ViewState.Success -> {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        cells = GridCells.Fixed(2),
                        contentPadding = PaddingValues(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items((uiState as ViewState.Success<List<Appliance>>).data) { appliance ->
                            ItemAppliance(
                                appliance = appliance,
                                applianceClicked = { navController.navigate(
                                    MainDestinations.APPLIANCE_ROUTE,
                                    Arguments.APPLIANCE to appliance) }
                            )
                        }
                    }
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

@Composable
fun AppliancesFab(navController: NavController) {
    FloatingActionButton(
        modifier = Modifier
            .animateContentSize()
            .padding(bottom = 16.dp),
        onClick = { navController.navigate(MainDestinations.NEW_APPLIANCE_ROUTE) },
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add new appliance",
            tint = Color.White,
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemAppliance(appliance: Appliance, applianceClicked: (Appliance) -> Unit) {
    MyCard(
        onClick = { applianceClicked(appliance) }) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(5.dp)

        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .requiredSize(100.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
                    .background(Color(appliance.color)),
            ) {
                Text(
                    if (appliance.name.isEmpty()) ""
                    else appliance.name.first().uppercase(),
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    style = typography.h4,
                )
            }
            Text(appliance.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            //Text(user.email)
        }
    }
}
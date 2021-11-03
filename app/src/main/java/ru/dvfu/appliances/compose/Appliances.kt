package ru.dvfu.appliances.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.viewmodels.AppliancesViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Role
import ru.dvfu.appliances.model.repository.entity.User

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun Appliances(navController: NavController, backPress: () -> Unit, modifier: Modifier = Modifier) {

    val viewModel = getViewModel<AppliancesViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val refreshing by remember { viewModel.isRefreshing }

    val user: User? by viewModel.user.collectAsState(User())
    //val isRefreshing by viewModel.isRefreshing.collectAsState()

    /*LaunchedEffect(refreshing) {
        if (refreshing) {

            delay(2000)
            refreshing = false
        }
    }*/

    val appliances by viewModel.appliancesList.collectAsState()
    Scaffold(
        topBar = { ScheduleAppBar(stringResource(R.string.appliances), backClick = backPress) },
        floatingActionButton = { if (user!!.role >= Role.ADMIN.ordinal) AppliancesFab(navController) },
        modifier = Modifier.fillMaxSize(),
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(refreshing),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize().background(Color(0XFFE3DAC9)),
        ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                cells = GridCells.Fixed(2),
            ) {
                items(appliances) { appliance ->
                    ItemAppliance(
                        appliance = appliance,
                        applianceClicked = { navController.navigate(
                            MainDestinations.APPLIANCE_ROUTE,
                            Arguments.APPLIANCE to appliance) }
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

@Composable
fun AppliancesFab(navController: NavController) {
    FloatingActionButton(
        modifier = Modifier.animateContentSize().padding(bottom = 15.dp),
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
    MyCard(modifier = Modifier.padding(4.dp),
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
                modifier = Modifier.requiredSize(100.dp).clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
                    .background(/*Color(appliance.color)*/Color.Yellow),
            ) {
                Text(
                    appliance.name.first().toString(),
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    style = typography.h4,
                )
            }
            Text(appliance.name, fontWeight = FontWeight.Normal, fontSize = 20.sp)
            //Text(user.email)
        }
    }
}
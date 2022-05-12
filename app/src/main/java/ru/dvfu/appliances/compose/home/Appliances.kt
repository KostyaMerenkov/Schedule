package ru.dvfu.appliances.compose

import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.appliance.ApplianceNameSet
import ru.dvfu.appliances.compose.components.FullscreenLoading
import ru.dvfu.appliances.compose.viewmodels.AppliancesViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.ViewState

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun Appliances(navController: NavController, backPress: () -> Unit, modifier: Modifier = Modifier) {

    val viewModel: AppliancesViewModel = getViewModel()
    val appliancesState by viewModel.appliancesState.collectAsState()

    val user: User by viewModel.user.collectAsState(User())


    Scaffold(
        backgroundColor = Color(0XFFE3DAC9),
        topBar = {
            if (user.role >= Roles.ADMIN.ordinal) ScheduleAppBar(stringResource(R.string.appliances),
                backClick = backPress,
                actionAdd = true,
                addClick = { navController.navigate(MainDestinations.NEW_APPLIANCE_ROUTE) })
            else ScheduleAppBar(stringResource(R.string.appliances), backClick = backPress)
        },
    ) {
        Crossfade(targetState = appliancesState) {
            when (it) {
                is ViewState.Error -> {}
                is ViewState.Loading -> {
                    FullscreenLoading()
                }
                is ViewState.Success -> {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        cells = GridCells.Fixed(2),
                        contentPadding = PaddingValues(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(it.data) { appliance ->
                            ItemAppliance(
                                appliance = appliance,
                                applianceClicked = {
                                    navController.navigate(
                                        MainDestinations.APPLIANCE_ROUTE,
                                        Arguments.APPLIANCE to appliance
                                    )
                                }
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
    val contentAlpha = if (appliance.active) ContentAlpha.high else ContentAlpha.disabled
    CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
        MyCard(
            onClick = { applianceClicked(appliance) }) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .aspectRatio(0.9f)
                    .height(150.dp)
                    .padding(10.dp)

            ) {
                ApplianceImage(appliance, modifier = Modifier.requiredSize(100.dp))
                ApplianceName(appliance)
            }
        }
    }
}

@Composable
fun ApplianceName(appliance: Appliance, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Center) {
    Text(
        modifier = modifier,
        text = appliance.name,
        maxLines = 1,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    )
}

@Composable
fun ApplianceImage(appliance: Appliance, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .aspectRatio(1f)
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
}

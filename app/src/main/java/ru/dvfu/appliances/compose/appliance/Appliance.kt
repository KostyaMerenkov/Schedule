package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.compose.viewmodels.ApplianceViewModel
import ru.dvfu.appliances.model.repository.entity.Roles

@ExperimentalMaterialApi
@ExperimentalPagerApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Appliance(navController: NavController, upPress: () -> Unit, appliance: Appliance) {
    val viewModel: ApplianceViewModel by viewModel()

    viewModel.setAppliance(appliance)

    val updatedAppliance by viewModel.appliance.collectAsState()

    var infoDialogState = remember { mutableStateOf(false) }

    val user: User by viewModel.currentUser.collectAsState(User())

    if (infoDialogState.value) ApplianceInfoDialog(infoDialogState, updatedAppliance)

    val tabs = listOf(TabItem.Users, TabItem.SuperUsers)
    val pagerState = rememberPagerState(pageCount = tabs.size)

    Scaffold(topBar = {
        ApplianceTopBar(user, updatedAppliance, viewModel, upPress)
    },
        modifier = Modifier.fillMaxSize().background(Color(0XFFE3DAC9))) {
        Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
            Surface(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                //color = Color.White
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentHeight()
                ) {
                    if (updatedAppliance.description.isNotEmpty()) {
                        IconButton(onClick = { infoDialogState.value = true }) {
                            Icon(Icons.Default.Info, "")
                        }
                    }
                    Text(
                        updatedAppliance.name,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h4,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }
            Column() {
                Tabs(tabs = tabs, pagerState = pagerState)
                Box(modifier = Modifier.fillMaxSize()) {
                    //BackgroundImage()
                    TabsContent(tabs = tabs, pagerState = pagerState, navController, updatedAppliance)
                }
            }
        }

    }
}

@Composable
fun ApplianceTopBar(
    user: User,
    appliance: Appliance,
    viewModel: ApplianceViewModel,
    upPress: () -> Unit
) {
    if (permitToDeleteAppliance(user, appliance)) {
        ScheduleAppBar(
            stringResource(R.string.appliance),
            backClick = upPress,
            actionDelete = true,
            deleteClick = { viewModel.deleteAppliance(); upPress() },
            elevation = 0.dp
        )
    } else {
        ScheduleAppBar(
            stringResource(R.string.appliance),
            backClick = upPress,
            elevation = 0.dp
        )
    }

}

fun permitToDeleteAppliance(user: User, appliance: Appliance) = appliance.superuserIds.contains(user.userId) ||  user.role == Roles.ADMIN.ordinal

@Composable
fun ApplianceInfoDialog(infoDialogState: MutableState<Boolean>, appliance: Appliance) {
    Dialog(onDismissRequest = { infoDialogState.value = false }) {
        Card(
            modifier = Modifier.padding(horizontal = 24.dp).wrapContentHeight(),
            shape = RoundedCornerShape(25.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    appliance.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6
                )
                Text(appliance.description)
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun Tabs(tabs: List<TabItem>, pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    // OR ScrollableTabRow()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colors.surface,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                //color = MaterialTheme.colors.onSurface,
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }) {
        tabs.forEachIndexed { index, tab ->
            // OR Tab()
            LeadingIconTab(
                icon = { Icon(imageVector = tab.icon, contentDescription = "",
                    /*tint = MaterialTheme.colors.primaryVariant*/ ) },
                text = { Text(stringResource(tab.titleRes), color = MaterialTheme.colors.onSurface) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabsContent(
    tabs: List<TabItem>,
    pagerState: PagerState,
    navController: NavController,
    appliance: Appliance
) {
    HorizontalPager(
        state = pagerState
    ) { page ->
        tabs[page].screen(navController, appliance)
    }
}


package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.DisabledByDefault
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import ru.dvfu.appliances.BuildConfig
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.*
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.home.EventDeleteDialog
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.compose.viewmodels.ApplianceDetailsViewModel
import ru.dvfu.appliances.compose.components.views.DefaultDialog
import ru.dvfu.appliances.compose.components.views.ModalLoadingDialog
import ru.dvfu.appliances.compose.components.views.TextDivider
import ru.dvfu.appliances.compose.home.booking_list.BookingUser
import ru.dvfu.appliances.model.repository.entity.isAdmin
import ru.dvfu.appliances.model.repository.entity.isUserSuperuserOrAdmin

@ExperimentalMaterialApi
@ExperimentalPagerApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ApplianceDetails(navController: NavController, upPress: () -> Unit, appliance: Appliance) {
    val viewModel: ApplianceDetailsViewModel by viewModel()
    viewModel.setAppliance(appliance)

    val createdUser by viewModel.createdUser.collectAsState()
    val noApplianceEvents by viewModel.noApplianceEvents.collectAsState()
    val updatedAppliance by viewModel.appliance.collectAsState()

    var infoDialogState by remember { mutableStateOf(false) }
    val user: User by viewModel.currentUser.collectAsState(User())

    if (infoDialogState) ApplianceInfoDialog(updatedAppliance) { infoDialogState = false }

    var applianceDeleteDialog by remember { mutableStateOf(false) }
    if (applianceDeleteDialog) {
        ApplianceDeleteDialog(onDismiss = { applianceDeleteDialog = false }) {
            viewModel.deleteAppliance()
        }
    }

    val uiState = viewModel.uiState.collectAsState()

    if (uiState.value is UiState.InProgress) ModalLoadingDialog()

    LaunchedEffect(uiState.value) {
        if (uiState.value is UiState.Success) upPress()
    }

    val tabs = listOf(/*TabItem.Users, */TabItem.SuperUsers)
    val pagerState = rememberPagerState()

    Scaffold(
        topBar = {
            ApplianceTopBar(
                user,
                updatedAppliance,
                noApplianceEvents = noApplianceEvents,
                upPress,
                deleteClick = { applianceDeleteDialog = true },
                disableEnableClick = viewModel::disableEnable
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFFE3DAC9))
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentHeight()
            ) {
                if (updatedAppliance.description.isNotEmpty()) {
                    IconButton(onClick = { infoDialogState = true }) {
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
            createdUser?.let {
                BookingUser(it, header = "Владелец прибора") { navController.navigate(MainDestinations.USER_DETAILS_ROUTE, Arguments.USER to it) }
                //Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
                Spacer(modifier = Modifier.size(8.dp))
            }

            Column() {
                Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
                Tabs(tabs = tabs, pagerState = pagerState)
                Box(modifier = Modifier.fillMaxSize()) {
                    TabsContent(
                        tabs = tabs,
                        pagerState = pagerState,
                        navController,
                        updatedAppliance
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplianceDeleteDialog(onDismiss: () -> Unit, function: () -> Unit) {
    DefaultDialog(
        primaryText = stringResource(id = R.string.appliance_delete_sure),
        positiveButtonText = stringResource(id = R.string.Yes),
        negativeButtonText = stringResource(id = R.string.No),
        onPositiveClick = { function(); onDismiss() },
        onNegativeClick = { onDismiss() },
        onDismiss = onDismiss
    ) {}
}

@Composable
fun ApplianceTopBar(
    user: User,
    appliance: Appliance,
    noApplianceEvents: Boolean,
    upPress: () -> Unit,
    deleteClick: () -> Unit,
    disableEnableClick: (Boolean) -> Unit,
) {

    ScheduleAppBar(
        stringResource(R.string.appliance),
        backClick = upPress,
        actionDelete = user.isAdmin && noApplianceEvents,
        deleteClick = deleteClick,
        elevation = 0.dp,
        actions = {
            if (appliance.isUserSuperuserOrAdmin(user))
                IconButton(onClick = { disableEnableClick(!appliance.active) }) {
                    when (appliance.active) {
                        true -> Icon(Icons.Default.DoNotDisturb, "")
                        else -> Icon(Icons.Default.Autorenew, "")
                    }
                }
        }
    )

}

@Composable
fun ApplianceInfoDialog(appliance: Appliance, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .wrapContentHeight(),
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
                icon = {
                    Icon(
                        imageVector = tab.icon, contentDescription = "",
                        /*tint = MaterialTheme.colors.primaryVariant*/
                    )
                },
                text = {
                    Text(
                        stringResource(tab.titleRes),
                        color = MaterialTheme.colors.onSurface
                    )
                },
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
        state = pagerState,
        count = tabs.size
    ) { page ->
        tabs[page].screen(navController, appliance)
    }
}


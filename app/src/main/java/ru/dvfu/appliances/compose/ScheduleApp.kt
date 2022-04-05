package ru.dvfu.appliances.compose

import Drawer
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.compose.appliance.AddUser
import ru.dvfu.appliances.compose.appliance.ApplianceDetails
import ru.dvfu.appliances.compose.appliance.NewAppliance
import ru.dvfu.appliances.compose.home.*
import ru.dvfu.appliances.compose.viewmodels.MainViewModel

@OptIn(ExperimentalComposeUiApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun ScheduleApp() {
    MaterialTheme {
        val appStateHolder = rememberAppStateHolder()
        val viewModel: MainScreenViewModel = getViewModel()
        val scope = rememberCoroutineScope()

        val result = remember { mutableStateOf("") }
        val selectedItem = remember { mutableStateOf("upload") }

        Scaffold(
            /*floatingActionButton = {
                if (appStateHolder.shouldShowFab) {
                    FloatingActionButton(
                        onClick = { result.value = "FAB clicked" },
                        backgroundColor = Color(0xFFFF8C00),

                    ) {
                        Icon(Icons.Filled.Add, "")
                    }
                }
            },*/
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.Center,
            drawerContent = {
                Drawer(
                    scope = scope,
                    scaffoldState = appStateHolder.scaffoldState,
                    navController = appStateHolder.navController
                )
            },
            bottomBar = {
                if (appStateHolder.shouldShowBottomBar) {
                    ScheduleBottomBar(
                        tabs = appStateHolder.bottomBarTabs,
                        currentRoute = appStateHolder.currentRoute!!,
                        navigateToRoute = appStateHolder::navigateToBottomBarRoute
                    )
                }
                //ScheduleBottomBar(result, selectedItem, fabShape)
            },
            scaffoldState = appStateHolder.scaffoldState
        ) { innerPaddingModifier ->
            //Spacer(modifier = Modifier.statusBarsHeight())
            NavHost(
                navController = appStateHolder.navController,
                startDestination = MainDestinations.HOME_ROUTE,
                modifier = Modifier.padding(innerPaddingModifier)
            ) {
                NavGraph(
                    navController = appStateHolder.navController,
                    upPress = appStateHolder::upPress,
                    openDrawer = { scope.launch { appStateHolder.scaffoldState.drawerState.open() } }
                )
            }
        }
    }
}


@ExperimentalComposeUiApi
@OptIn(ExperimentalPagerApi::class)
@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
private fun NavGraphBuilder.NavGraph(
    openDrawer: () -> Unit,
    navController: NavController,
    backPress: () -> Unit = { navController.popBackStack() },
    upPress: () -> Unit
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSections.CALENDAR.route
    ) {
        addHomeGraph(
            navController = navController,
            openDrawer = openDrawer,
            backPress = upPress
        )
    }

    composable(MainDestinations.ADD_EVENT) {
        AddEvent(navController = navController)
    }

    composable(MainDestinations.ADD_BOOKING) {
        AddBooking(navController = navController)
    }

    composable(MainDestinations.EVENT_INFO) {
        EventInfo(navController, eventArg = it.requiredArg(Arguments.EVENT), backPress)
    }

    /*composable(MainDestinations.EVENT_INFO) {
        WeekCalendar(navController)
    }*/


    /*composable(MainDestinations.LOGIN_ROUTE) {
        LoginScreen(navController = navController)
    }*/

    composable(
        route = MainDestinations.APPLIANCE_ROUTE,
    ) { ApplianceDetails(navController, upPress, it.requiredArg(Arguments.APPLIANCE)) }

    composable(
        route = MainDestinations.ADD_USER_TO_APPLIANCE,
    ) { AddUser(navController, it.requiredArg(Arguments.APPLIANCE)) }

    composable(
        route = MainDestinations.ADD_SUPERUSER_TO_APPLIANCE,
    ) { AddUser(navController, it.requiredArg(Arguments.APPLIANCE), areSuperUsers = true) }

    composable(
        route = MainDestinations.APPLIANCES_ROUTE,
    ) { Appliances(navController, upPress) }

    composable(
        route = MainDestinations.NEW_APPLIANCE_ROUTE,
    ) { NewAppliance(upPress) }

    composable(
        route = MainDestinations.USER_DETAILS_ROUTE,
    ) { UserDetails(navController, upPress, it.requiredArg(Arguments.USER)) }

    composable(
        route = MainDestinations.USERS_ROUTE,
    ) { Users(navController, upPress) }

    composable(
        route = MainDestinations.BOOKING_LIST
    ) {
        BookingList(navController = navController)
    }

    composable(
        route = MainDestinations.SETTINGS_ROUTE,
    ) { Settings(navController, upPress) }

}



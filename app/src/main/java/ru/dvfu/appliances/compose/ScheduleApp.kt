package ru.dvfu.appliances.compose

import Drawer
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import ru.dvfu.appliances.compose.appliance.AddUser
import ru.dvfu.appliances.compose.appliance.ApplianceDetails
import ru.dvfu.appliances.compose.appliance.NewAppliance
import ru.dvfu.appliances.compose.event_calendar.EventInfo
import ru.dvfu.appliances.compose.home.AddEvent
import ru.dvfu.appliances.compose.home.MainScreen

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun ScheduleApp() {
    //ProvideWindowInsets {
    MaterialTheme {
        val appStateHolder = rememberAppStateHolder()
        //var visible by remember { mutableStateOf(false) }
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
    upPress: () -> Unit,
    openDrawer: () -> Unit,
    navController: NavController,
    backPress: () -> Unit = { navController.popBackStack() }
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSections.CALENDAR.route
    ) {
        addHomeGraph(navController, openDrawer = openDrawer, upPress)
    }

    composable(MainDestinations.ADD_EVENT) {
        AddEvent(navController = navController)
    }

    composable(MainDestinations.EVENT_INFO) {
        EventInfo()
    }

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
        route = MainDestinations.SETTINGS_ROUTE,
    ) { Settings(navController, upPress) }
}
package ru.dvfu.appliances.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.compose.appliance.AddUsersToAppliance
import ru.dvfu.appliances.compose.appliance.ApplianceDetails
import ru.dvfu.appliances.compose.appliance.NewAppliance
import ru.dvfu.appliances.compose.home.*
import ru.dvfu.appliances.compose.home.booking_list.BookingList
import ru.dvfu.appliances.compose.profile.EditProfile
import java.time.LocalDate

@OptIn(ExperimentalComposeUiApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun ScheduleApp() {
    val appStateHolder = rememberAppStateHolder()
    val viewModel: MainScreenViewModel = getViewModel()
    val scope = rememberCoroutineScope()

    Scaffold(
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            if (appStateHolder.shouldShowBottomBar) {
                ScheduleBottomBar(
                    tabs = appStateHolder.bottomBarTabs,
                    currentRoute = appStateHolder.currentRoute!!,
                    navigateToRoute = appStateHolder::navigateToBottomBarRoute
                )
            }
        },
        scaffoldState = appStateHolder.scaffoldState
    ) { innerPaddingModifier ->
        NavHost(
            navController = appStateHolder.navController,
            startDestination = MainDestinations.HOME_ROUTE,
            modifier = Modifier.padding(innerPaddingModifier)
        ) {
            NavGraph(
                navController = appStateHolder.navController,
                upPress = appStateHolder::upPress,
            )
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
            backPress = upPress
        )
    }

    composable(MainDestinations.ADD_EVENT) {
        val selectedDate =
            it.arguments?.getParcelable<SelectedDate>(Arguments.DATE)?.value ?: LocalDate.now()
        AddEvent(selectedDate = selectedDate, upPress)
    }

    composable(MainDestinations.EVENT_INFO) {
        EventInfoScreen(navController, eventArg = it.requiredArg(Arguments.EVENT), backPress)
    }

    composable(MainDestinations.EDIT_PROFILE) {
        EditProfile() {
            navController.popBackStack()
        }
    }


    /*composable(MainDestinations.LOGIN_ROUTE) {
        LoginScreen(navController = navController)
    }*/

    composable(
        route = MainDestinations.APPLIANCE_ROUTE,
    ) { ApplianceDetails(navController, upPress, it.requiredArg(Arguments.APPLIANCE)) }

    composable(
        route = MainDestinations.ADD_USER_TO_APPLIANCE,
    ) { AddUsersToAppliance(navController, it.requiredArg(Arguments.APPLIANCE)) }

    composable(
        route = MainDestinations.ADD_SUPERUSER_TO_APPLIANCE,
    ) {
        AddUsersToAppliance(
            navController,
            it.requiredArg(Arguments.APPLIANCE),
            areSuperUsers = true
        )
    }

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



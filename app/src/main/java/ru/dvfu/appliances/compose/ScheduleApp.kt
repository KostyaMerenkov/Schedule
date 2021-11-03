package ru.dvfu.appliances.compose

import Drawer
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

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
        val fabShape = RoundedCornerShape(50)

        Scaffold(
            floatingActionButton = {
                if (appStateHolder.shouldShowFab) {
                    FloatingActionButton(
                        onClick = { result.value = "FAB clicked" },
                        shape = fabShape,
                        backgroundColor = Color(0xFFFF8C00)
                    ) {
                        Icon(Icons.Filled.Add, "")
                    }
                }
            },
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
                        result, selectedItem, fabShape,
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
                //modifier = Modifier.padding(innerPaddingModifier)
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


@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalMaterialApi
private fun NavGraphBuilder.NavGraph(
    upPress: () -> Unit,
    openDrawer: () -> Unit,
    navController: NavController,
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSections.CALENDAR.route
    ) {
        addHomeGraph(navController, openDrawer = openDrawer, backPress = {})
    }

    /*composable(MainDestinations.LOGIN_ROUTE) {
        LoginScreen(navController = navController)
    }*/

    composable(
        route = MainDestinations.APPLIANCES_ROUTE,
    ) { Appliances(navController) }

    composable(
        route = MainDestinations.NEW_APPLIANCE_ROUTE,
    ) { NewAppliance(upPress) }

    composable(
        route = MainDestinations.USERS_ROUTE,
    ) { Users(navController) }
}

@Composable
fun Calendar(openDrawer: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Bottom app bar + FAB") },
            navigationIcon = {
                IconButton(onClick = { openDrawer() }
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = "")
                }
            },

            backgroundColor = Color(0xFFFF5470),
            elevation = AppBarDefaults.TopAppBarElevation
        )
    }) {
        Box(
            Modifier
                .background(Color(0XFFE3DAC9))
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Text(
                text = "Calendar",
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

}
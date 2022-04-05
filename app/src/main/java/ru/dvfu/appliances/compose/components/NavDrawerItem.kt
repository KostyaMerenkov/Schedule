package ru.dvfu.appliances.compose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ru.dvfu.appliances.compose.HomeSections
import ru.dvfu.appliances.compose.MainDestinations

sealed class NavDrawerItem(val route: String, val icon: ImageVector, val title: String) {
    object EventCalendar : NavDrawerItem(MainDestinations.HOME_ROUTE, Icons.Default.Home, "Home")
    object WeekCalendar : NavDrawerItem(MainDestinations.HOME_ROUTE + "/" + MainDestinations.WEEK_CALENDAR, Icons.Default.Home, "Home")
    object Users : NavDrawerItem(MainDestinations.USERS_ROUTE, Icons.Default.VerifiedUser, "Users")
    object Appliances : NavDrawerItem(MainDestinations.APPLIANCES_ROUTE, Icons.Default.SettingsApplications, "Appliances")
    object Settings : NavDrawerItem(MainDestinations.SETTINGS_ROUTE, Icons.Default.Settings, "Settings")
}

val ALL_DRAWER_ITEMS = listOf(
    NavDrawerItem.EventCalendar,
    NavDrawerItem.WeekCalendar,
    NavDrawerItem.Users,
    NavDrawerItem.Appliances,
    NavDrawerItem.Settings
)
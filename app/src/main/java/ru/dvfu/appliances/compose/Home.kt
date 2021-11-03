package ru.dvfu.appliances.compose

import androidx.compose.ui.tooling.preview.Preview

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import ru.dvfu.appliances.R

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
fun NavGraphBuilder.addHomeGraph(
    navController: NavController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    backPress: () -> Unit
) {
    composable(
        HomeSections.CALENDAR.route
    ) { from ->
        Calendar(openDrawer)
    }
    composable(
        HomeSections.APPLIANCES.route
    ) { from ->
        Appliances(navController)
    }
    composable(
        HomeSections.USERS.route
    ) { from ->
        Users(navController)
    }
    /*composable(HomeSections.NOTES.route) { from ->
        Notes(onSnackClick = { id -> onSnackSelected(id, from) }, modifier, navController)
    }*/
    /*composable(HomeSections.WEATHER.route) { from ->
        Weather(onSnackClick = { id -> onSnackSelected(id, from) }, modifier, navController)
        { navController.popBackStack() }
    }*/
    composable(HomeSections.PROFILE.route) {
        Profile(navController, modifier, { navController.popBackStack() })
    }
}

enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    CALENDAR(R.string.calendar, Icons.Outlined.Home, "home/calendar"),
    //NOTES(R.string.notes, Icons.Outlined.Menu, "home/notes"),
    //WEATHER(R.string.weather, Icons.Outlined.WbSunny, "home/weather"),
    APPLIANCES(R.string.appliances, Icons.Outlined.Apartment, "home/appliances"),
    USERS(R.string.users, Icons.Outlined.SupervisedUserCircle, "home/users"),
    PROFILE(R.string.profile, Icons.Outlined.VerifiedUser, "home/profile")
}

@Composable
fun ScheduleBottomBar(
    result: MutableState<String>,
    selectedItem: MutableState<String>,
    fabShape: RoundedCornerShape,
    tabs: Array<HomeSections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
//    color: Color = Theme.colors.iconPrimary,
//    contentColor: Color = Theme.colors.iconInteractive
) {
    val routes = remember { tabs.map { it.route } }
    val currentSection = tabs.first { it.route == currentRoute }

    BottomAppBar(
        cutoutShape = fabShape,
        content = {
            BottomNavigation() {

                tabs.forEach { section ->
                    val selected = section == currentSection
                    val tint by animateColorAsState(
                        if (selected) {
                            Color.Black
                        } else {
                            Color.Black
                        }
                    )

                    BottomNavigationItem(
                        icon = {
                            Icon(section.icon, section.name, /*tint = tint*/)
                        },
                        label = {
                            Text(
                                stringResource(section.title).uppercase(
                                    ConfigurationCompat.getLocales(LocalConfiguration.current)
                                        .get(0)
                                ),
                                maxLines = 1, /*color = tint*/
                            )
                        },
                        selected = selected,
                        onClick = {
                            navigateToRoute(section.route)
                        },
                        alwaysShowLabel = false,
                    )
                }
            }
               /* FishingNotesBottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = section.icon,
                            tint = tint,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(section.title).uppercase(
                                ConfigurationCompat.getLocales(
                                    LocalConfiguration.current
                                ).get(0)
                            ),
                            color = tint,
                            style = MaterialTheme.typography.button,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onSelected = { navigateToRoute(section.route) },
                    animSpec = springSpec,
                    modifier = BottomNavigationItemPadding
                        .clip(BottomNavIndicatorShape)
                )
            }*/

            /*BottomNavigation() {
                BottomNavigationItem(
                    icon = {
                        Icon(Icons.Filled.Home , "")
                    },
                    label = { Text(text = "Favorite")},
                    selected = selectedItem.value == "favorite",
                    onClick = {
                        result.value = "Favorite icon clicked"
                        selectedItem.value = "favorite"
                    },
                    alwaysShowLabel = false
                )

                BottomNavigationItem(
                    icon = {
                        Icon(Icons.Filled.Upload ,  "")
                    },


                    label = { Text(text = "Upload")},
                    selected = selectedItem.value == "upload",
                    onClick = {
                        result.value = "Upload icon clicked"
                        selectedItem.value = "upload"
                    },
                    alwaysShowLabel = false
                )
            }*/
        }
    )
}

private val TextIconSpacing = 2.dp
private val BottomNavHeight = 56.dp
private val BottomNavLabelTransformOrigin = TransformOrigin(0f, 0.5f)
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)
private val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

@Preview
@Composable
private fun FishingNotesBottomNavPreview() {
    MaterialTheme {
//        ScheduleBottomBar(
//            tabs = HomeSections.values(),
//            currentRoute = "home/map",
//            navigateToRoute = { }
//        )
    }
}

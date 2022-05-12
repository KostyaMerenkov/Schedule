package ru.dvfu.appliances.compose

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.SupervisedUserCircle
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.viewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.home.HomeScreen
import ru.dvfu.appliances.compose.viewmodels.WeekCalendarViewModel

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
fun NavGraphBuilder.addHomeGraph(
    backPress: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
) {

    composable(
        HomeSections.CALENDAR.route
    ) {
        HomeScreen(navController, backPress)
    }

    composable(
        HomeSections.APPLIANCES.route
    ) { from ->
        Appliances(navController, backPress)
    }
    composable(
        HomeSections.USERS.route
    ) { from ->
        Users(navController, backPress)
    }

    composable(HomeSections.PROFILE.route) {
        Profile(navController, modifier, backPress)
    }
}

enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    CALENDAR(R.string.calendar, Icons.Outlined.Home, "home/calendar"),
    APPLIANCES(R.string.appliances, Icons.Outlined.Apartment, "home/appliances"),
    USERS(R.string.users, Icons.Outlined.SupervisedUserCircle, "home/users"),
    PROFILE(R.string.profile, Icons.Outlined.VerifiedUser, "home/profile")
}

@Composable
fun ScheduleBottomBar(
    tabs: Array<HomeSections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
//    color: Color = Theme.colors.iconPrimary,
//    contentColor: Color = Theme.colors.iconInteractive
) {
    val routes = remember { tabs.map { it.route } }
    val currentSection = tabs.first { it.route == currentRoute }
    val fabShape = CircleShape

    BottomAppBar(
        //cutoutShape = fabShape,
        content = {
            BottomNavigation() {
                tabs.forEach { section ->
                    val selected = section == currentSection
                    BottomNavigationItem(
                        icon = {
                            Icon(section.icon, section.name /*tint = tint*/)
                        },
                        label = {
                            Text(
                                stringResource(section.title),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis, /*color = tint*/
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
        }
    )
}

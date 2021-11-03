import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.Appliances
import ru.dvfu.appliances.compose.Profile
import ru.dvfu.appliances.compose.Users
import ru.dvfu.appliances.ui.components.ALL_DRAWER_ITEMS
import ru.dvfu.appliances.ui.components.DrawerItem

@Composable
fun Drawer(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {
    val items = ALL_DRAWER_ITEMS
    Column(
        /*modifier = Modifier
            .background(colorResource(id = R.color.design_default_color_primary)), */
        content = {
            // Header
            Image(
                Icons.Default.VerifiedUser,
                contentDescription = "R.drawable.logo.toString()",
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            // Space between
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )
            // List of navigation items
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                DrawerItem(item = item, selected = (currentRoute == item.route), onItemClick = {
                    navController.navigate(item.route) {
                        /*// Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true*/
                    }
                    // Close drawer
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                })
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Schedule App",
                /*color = Color.White,*/
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    )
}



/*
package ru.dvfu.appliances.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsHeight

@Composable
fun ColumnScope.ScheduleDrawer(
    */
/*onProfileClicked: (String) -> Unit,
    onChatClicked: (String) -> Unit*//*

) {
    // Use statusBarsHeight() to add a spacer which pushes the drawer content
    // below the status bar (y-axis)
    Spacer(Modifier.statusBarsHeight())
    DrawerHeader()
    Divider()
    DrawerItemHeader("Lists")
    ChatItem("composers", true) { onChatClicked("composers") }
    ChatItem("droidcon-nyc", false) { onChatClicked("droidcon-nyc") }
    DrawerItemHeader("Recent Profiles")
//    ProfileItem("Ali Conors (you)", meProfile.photo) { onProfileClicked(meProfile.userId) }
//    ProfileItem("Taylor Brooks", colleagueProfile.photo) {
//        onProfileClicked(colleagueProfile.userId)
//    }
}

@Composable
private fun DrawerHeader() {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = CenterVertically) {
        */
/*Image(
            painter = painterResource(id = R.drawable.ic_jetchat),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.jetchat_logo),
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp)
        )*//*

    }
}

@Composable
private fun DrawerItemHeader(text: String) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(text, style = MaterialTheme.typography.caption, modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun ChatItem(text: String, selected: Boolean, onChatClicked: () -> Unit) {
    val background = if (selected) {
        Modifier.background(MaterialTheme.colors.primary.copy(alpha = 0.08f))
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .then(background)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onChatClicked),
        verticalAlignment = CenterVertically
    ) {
        val iconTint = if (selected) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
        }
//        Icon(
//            painter = painterResource(id = R.drawable.ic_jetchat),
//            tint = iconTint,
//            modifier = Modifier.padding(8.dp),
//            contentDescription = null
//        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text,
                style = MaterialTheme.typography.body2,
                color = if (selected) MaterialTheme.colors.primary else LocalContentColor.current,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun ProfileItem(text: String, @DrawableRes profilePic: Int?, onProfileClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onProfileClicked),
        verticalAlignment = CenterVertically
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            val widthPaddingModifier = Modifier.padding(8.dp).size(24.dp)
            if (profilePic != null) {
                Image(
                    painter = painterResource(id = profilePic),
                    modifier = widthPaddingModifier.then(Modifier.clip(CircleShape)),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            } else {
                Spacer(modifier = widthPaddingModifier)
            }
            Text(text, style = MaterialTheme.typography.body2, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
@Preview
fun DrawerPreview() {
    //JetchatTheme {
    Surface {
        Column {
            ScheduleDrawer()
        }
    }
    //}
}

@Composable
@Preview
fun DrawerPreviewDark() {
    //JetchatTheme(isDarkTheme = true) {
    Surface {
        Column {
            ScheduleDrawer()
        }
    }
    //}
}*/

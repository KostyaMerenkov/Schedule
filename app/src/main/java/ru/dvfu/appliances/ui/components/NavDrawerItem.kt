package ru.dvfu.appliances.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavDrawerItem(var route: String, var icon: ImageVector, var title: String) {
    object Home : NavDrawerItem("home", Icons.Default.Cake, "Home")
    object Music : NavDrawerItem("music", Icons.Default.Cake, "Music")
    object Movies : NavDrawerItem("movies", Icons.Default.Cake, "Movies")
    object Books : NavDrawerItem("books", Icons.Default.Cake, "Books")
    object Profile : NavDrawerItem("profile", Icons.Default.Cake, "Profile")
    object Settings : NavDrawerItem("settings", Icons.Default.Cake, "Settings")
}
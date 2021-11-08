package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import ru.dvfu.appliances.R
import ru.dvfu.appliances.model.repository.entity.Appliance

typealias ComposableFun = @Composable (navController: NavController, appliance: Appliance) -> Unit

sealed class TabItem(var icon: Int, var titleRes: Int, var screen: ComposableFun) {

    @ExperimentalAnimationApi
    object Users : TabItem(R.drawable.ic_guest, R.string.users, { navController, appliance ->
        ApplianceUsers(navController = navController, appliance)
    })


    @ExperimentalAnimationApi
    object SuperUsers :
        TabItem(R.drawable.ic_menu_gallery, R.string.superusers, { navController, appliance ->
            ApplianceSuperUsers(navController = navController, appliance)
        })
}
package ru.dvfu.appliances.compose

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.koin.androidx.compose.get
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.compose.viewmodels.UserDetailsViewModel

@Composable
fun UserDetails(navController: NavController, upPress: () -> Unit, user: User) {

    val viewModel: UserDetailsViewModel = get()
    //viewModel.appliance.value = appliance

    Scaffold(topBar = {
        ScheduleAppBar(user.email, upPress)
    }) {
        Text("User: " + user.userName)
    }
}
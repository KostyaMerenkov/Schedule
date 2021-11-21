package ru.dvfu.appliances.compose.appliance

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon

import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.runtime.*
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel

import ru.dvfu.appliances.compose.ItemUser
import ru.dvfu.appliances.compose.viewmodels.AddUserViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun AddUser(navController: NavController) {
    val viewModel = getViewModel<AddUserViewModel>()
    val users by viewModel.usersList.collectAsState()


    Scaffold {
        LazyColumn {
            items(users.size) {  i ->
                var isSelected by remember { mutableStateOf(false) }

                Row {
                    ItemUser(users[i]) { isSelected = !isSelected }
                    if (isSelected) Icon(Icons.Default.CheckCircleOutline,  "")


                }
            }
        }
    }
}
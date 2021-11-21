package ru.dvfu.appliances.compose.appliance

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.R
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel

import ru.dvfu.appliances.compose.ItemUser
import ru.dvfu.appliances.compose.MyCard
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.viewmodels.AddUserViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.BaseViewState

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun AddUser(
    navController: NavController,
    appliance: Appliance,
    superuser: Boolean = false,
) {
    val viewModel = getViewModel<AddUserViewModel>()
    val uiState = viewModel.uiState.collectAsState()
    val users by viewModel.usersList.collectAsState()

    LaunchedEffect(uiState.value) {
        when (uiState.value) {
            is BaseViewState.Success<*> -> {
                delay(300)
                isSuccess = true
                isLoading = false
                delay((MainActivity.splashFadeDurationMillis * 2).toLong())

                if ((uiState.value as BaseViewState.Success<*>).data as User? != null) {
                    visible = false
                    delay((MainActivity.splashFadeDurationMillis * 2).toLong())

                    navController.navigate(MainDestinations.HOME_ROUTE){
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            }
            is BaseViewState.Loading -> isLoading = true
            is BaseViewState.Error -> {
                scaffoldState.snackbarHostState.showSnackbar(errorString)
            }  //TODO: logger.log((uiState.value as BaseViewState.Error).error)
        }
    }

    val selectedUsers = remember { mutableStateListOf<User>() }

    var usersWithSelection by remember {
        mutableStateOf(
            users.map {
                UserItem(it, false)
            })
    }


    Scaffold(topBar = {
        ScheduleAppBar("Add users to appliance", navController::popBackStack)
    },
        floatingActionButton = {
            when (uiState.value) {
                is BaseViewState.Success<*> -> {
                    delay(300)
                    isSuccess = true
                    isLoading = false
                    delay((MainActivity.splashFadeDurationMillis * 2).toLong())

                    if ((uiState.value as BaseViewState.Success<*>).data as User? != null) {
                        visible = false
                        delay((MainActivity.splashFadeDurationMillis * 2).toLong())

                        navController.navigate(MainDestinations.HOME_ROUTE){
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                }
                is BaseViewState.Loading -> isLoading = true
            }
            if (!selectedUsers.isEmpty()) {
                FloatingActionButton(
                    onClick = { viewModel.addUsersToAppliance(appliance, selectedUsers.map { it.userId }) },
                    //shape = fabShape,
                    backgroundColor = Color(0xFFFF8C00),
                ) {
                    Icon(Icons.Filled.Check, "")
                }
            }


        }) {
        LazyColumn {
            items(users.size) { i ->
                var isSelected by remember { mutableStateOf(false) }

                ItemUserWithSelection(users[i], isSelected) {
                    isSelected = !isSelected
                    if (isSelected) selectedUsers.add(users[i])
                    else selectedUsers.remove(users[i])
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemUserWithSelection(user: User, isSelected: Boolean, userClicked: () -> Unit) {
    MyCard(onClick = userClicked) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .requiredHeight(60.dp)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            if (user.userPic.isNullOrEmpty()) {
                Icon(
//                        painter = rememberImagePainter(photo),
                    painterResource(ru.dvfu.appliances.R.drawable.ic_guest),
                    stringResource(ru.dvfu.appliances.R.string.No),
                    modifier = Modifier.clip(CircleShape)
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically),
                    //tint = secondaryFigmaColor
                )
            } else {
                Image(
                    painter = rememberImagePainter(user.userPic,
                        builder = {
                            crossfade(true)
                            placeholder(ru.dvfu.appliances.R.drawable.ic_launcher_foreground)
                            transformations(CircleCropTransformation())
                        }),
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically),
                    contentDescription = stringResource(ru.dvfu.appliances.R.string.user_photo),
                    //contentScale = ContentScale.Crop,


                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(user.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(user.email)
            }
            if (isSelected) Icon(
                Icons.Default.CheckCircleOutline,
                "",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
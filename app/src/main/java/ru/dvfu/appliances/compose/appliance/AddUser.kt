package ru.dvfu.appliances.compose.appliance

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import ru.dvfu.appliances.R

import ru.dvfu.appliances.compose.MyCard
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.viewmodels.AddUserViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.ViewState

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun AddUser(
    navController: NavController,
    appliance: Appliance,
    areSuperUsers: Boolean = false
) {
    val viewModel: AddUserViewModel by viewModel(parameters = { parametersOf(areSuperUsers, appliance) })
    val uiState = viewModel.uiState.collectAsState()
    val usersState by viewModel.usersState.collectAsState()
    val context = LocalContext.current

    val applianceUsers by remember {
        mutableStateOf(if (areSuperUsers) appliance.superuserIds else appliance.userIds)
    }

    LaunchedEffect(uiState.value) {
        when (uiState.value) {
            is BaseViewState.Success<*> -> {
                //delay(300)
                //isSuccess = true
                //isLoading = false
                //delay((MainActivity.splashFadeDurationMillis * 2).toLong())

                if ((uiState.value as BaseViewState.Success<*>).data != null) {
                    //visible = false
                    //delay((MainActivity.splashFadeDurationMillis * 2).toLong())
                    Toast.makeText(context, "Users added successfully", Toast.LENGTH_SHORT).show()
                    delay(500)
                    navController.popBackStack()
                }
            }
            //is BaseViewState.Loading -> isLoading = true
            is BaseViewState.Error -> {
                Toast.makeText(context, "Users added unsuccessfully", Toast.LENGTH_SHORT).show()
                //scaffoldState.snackbarHostState.showSnackbar(errorString)
            }  //TODO: logger.log((uiState.value as BaseViewState.Error).error)
            is BaseViewState.Loading -> {   }
        }
    }

    val selectedUsers = remember { mutableStateListOf<User>() }

    Scaffold(topBar = {
        ScheduleAppBar(
            title = if (areSuperUsers) stringResource(id = R.string.add_superuser)
            else stringResource(id = R.string.add_user), backClick = navController::popBackStack
        )
    },
        floatingActionButton = {
                FabWithLoading(showLoading = uiState.value is BaseViewState.Loading,
                onClick = { viewModel.addToAppliance(appliance, selectedUsers) }) {
                    Icon(Icons.Default.Check, contentDescription = Icons.Default.Check.name)
                }
        }) {
        AnimatedVisibility(visible = usersState is ViewState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        AnimatedVisibility(visible = usersState is ViewState.Success) {
            val users = (usersState as ViewState.Success).data
            UsersWithSelection(
                users = users,
                applianceUsers,
                addUser = { selectedUsers.add(it) },
                removeUser = { selectedUsers.remove(it) })
        }


    }
}

@Composable
fun FabWithLoading(showLoading: Boolean, onClick: ()-> Unit, content: @Composable ()-> Unit) {
    FloatingActionButton(
        onClick = onClick,
        backgroundColor = MaterialTheme.colors.secondary,
    ) {
        Crossfade(targetState = showLoading) {
            if (it) { CircularProgressIndicator() } else content.invoke()
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun UsersWithSelection(
    users: List<User>,
    applianceUsers: List<String>,
    addUser: (User) -> Unit,
    removeUser: (User) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp)
    ) {
        val usersToShow = users.filter { !applianceUsers.contains(it.userId) }

        usersToShow.ifEmpty {
            item { NoUsersView() }
        }
        items(usersToShow) { user ->
            var isSelected by remember { mutableStateOf(false) }

            ItemUserWithSelection(user, isSelected) {
                isSelected = !isSelected
                if (isSelected) addUser(user)
                else removeUser(user)
            }
        }
    }
}

@Composable
fun NoUsersView() {
    Text(text = "Нет пользователей")
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun ItemUserWithSelection(user: User, isSelected: Boolean, userClicked: () -> Unit) {
    MyCard(
        onClick = userClicked, modifier = Modifier
            .requiredHeight(80.dp)
            .fillMaxWidth(),
        backgroundColor = if (isSelected) Color.LightGray else MaterialTheme.colors.surface
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            UserImage(modifier = Modifier.size(50.dp), user)
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(user.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(user.email)
            }
            Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircleOutline,
                        "",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

        }
    }
}

@Composable
fun UserImage(modifier: Modifier, user: User) {
    Box(modifier= modifier, contentAlignment = Alignment.Center) {
        if (user.userPic.isEmpty()) {
            Icon(
                Icons.Default.Person, "",
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize()
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
                    .fillMaxSize(),
                contentDescription = stringResource(ru.dvfu.appliances.R.string.user_photo),
            )
        }
    }
}

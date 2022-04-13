package ru.dvfu.appliances.compose

import android.content.Context
import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.viewmodels.ProfileViewModel
import ru.dvfu.appliances.compose.views.DefaultDialog
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.activity.LoginActivity

@OptIn(ExperimentalComposeUiApi::class)
@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalCoilApi
@Composable
fun Profile(navController: NavController, modifier: Modifier = Modifier, backPress: () -> Unit) {
    val userDatastore: UserDatastore = get()

    val viewModel = getViewModel<ProfileViewModel>()
    val currentUser by viewModel.currentUser.collectAsState()

    val uiState = viewModel.uiState
    Scaffold(
        topBar = { ScheduleAppBar(stringResource(R.string.profile), backClick = backPress) },
        backgroundColor = Color(0XFFE3DAC9),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {

                Card(
                    elevation = 10.dp,
                    modifier = Modifier.padding(25.dp),
                    shape = RoundedCornerShape(25.dp),
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    Column() {
                        ProfileUserInfo(currentUser)
                    }

                }
                UserButtons(navController)
            }
        },
    )
}

@InternalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun UserButtons(navController: NavController) {
    val dialogOnLogout = rememberSaveable { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 80.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Bottom)
    ) {

        ColumnButton(Icons.Default.Edit, "Редактировать профиль")
        { navController.navigate(MainDestinations.EDIT_PROFILE) }


        Spacer(modifier = Modifier.size(15.dp))
        OutlinedButton(onClick = {
            dialogOnLogout.value = true
        }) { Text(stringResource(R.string.logout)) }
        Spacer(modifier = Modifier.size(30.dp))
        if (dialogOnLogout.value) LogoutDialog(dialogOnLogout)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@InternalCoroutinesApi
@Composable
fun LogoutDialog(dialogOnLogout: MutableState<Boolean>) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel = getViewModel<ProfileViewModel>()

    DefaultDialog(
        primaryText = "Выход из аккаунта",
        secondaryText = "Вы уверены, что хотите выйти из своего аккаунта?",
        onDismiss = { dialogOnLogout.value = false },
        positiveButtonText = stringResource(id = R.string.Yes),
        negativeButtonText = stringResource(id = R.string.No),
        onPositiveClick = {
            scope.launch {
                viewModel.logoutCurrentUser().collect { isLogout ->
                    if (isLogout) startLoginActivity(context)
                }
            }
        },
        onNegativeClick = { dialogOnLogout.value = false },
    )
}

@Composable
fun ColumnButton(image: ImageVector, name: String, click: () -> Unit) {
    OutlinedButton(
        onClick = click,
        modifier = Modifier.fillMaxWidth(),
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(image, name, modifier = Modifier.size(25.dp))
                Text(name, modifier = Modifier.padding(start = 10.dp))
            }
        })
}

@ExperimentalCoilApi
@Composable
fun ProfileUserInfo(user: User?) {
    //val user by
    user?.let { nutNullUser ->
        Crossfade(nutNullUser, animationSpec = tween(500)) { animatedUser ->
            UserNameAndImage(animatedUser)
        }
    } ?: Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}


@ExperimentalCoilApi
@Composable
fun UserNameAndImage(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (user.userPic.isNullOrEmpty()) {
            Icon(
                Icons.Default.Person, contentDescription = stringResource(R.string.user_photo),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(125.dp),
                //.align(Alignment.CenterVertically),
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
                contentDescription = stringResource(R.string.user_photo),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(125.dp)
                //contentScale = ContentScale.Crop,
            )
        }
        Text(
            text = when (user.anonymous) {
                true -> stringResource(R.string.anonymous_user)
                false -> user.userName
            }, style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
    }
}

private fun startLoginActivity(context: Context) {
    val intent = Intent(context, LoginActivity::class.java)
    context.startActivity(intent)
}

@ExperimentalCoilApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Preview("default")
//@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun ProfilePreview() {
    MaterialTheme {
        Profile(rememberNavController(), backPress = {})
    }
}


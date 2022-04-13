package ru.dvfu.appliances.compose.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.appliance.UserImage
import ru.dvfu.appliances.compose.components.DatePicker
import ru.dvfu.appliances.compose.components.GrayText
import ru.dvfu.appliances.compose.components.toDate
import ru.dvfu.appliances.compose.ui.theme.customColors
import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.compose.viewmodels.EditProfileViewModel
import ru.dvfu.appliances.compose.views.DefaultButton
import ru.dvfu.appliances.compose.views.DefaultDialog
import ru.dvfu.appliances.compose.views.ModalLoadingDialog
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.showError
import ru.dvfu.appliances.model.utils.toLocalDate
import ru.dvfu.appliances.ui.ViewState
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*


@SuppressLint("UnrememberedMutableState")
@Composable
fun EditProfile(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: EditProfileViewModel = getViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isChanged by viewModel.isChanged.collectAsState()
    val scrollState = rememberScrollState()

    var resetDialog by remember { mutableStateOf(false) }
    if (resetDialog) ResetDialog(
        onDismiss = { resetDialog = false },
        onReset = viewModel::resetChanges
    )


    var datePickerShown by remember { mutableStateOf(false) }
    if (datePickerShown) {
        DatePicker(context,
            date = currentUser.birthday.takeIf { it != 0L }?.toLocalDate()
                ?: LocalDate.now().minusYears(18),
            onDismiss = { datePickerShown = false }, onDateSet = {
                viewModel.birthdaySelected(it.atStartOfDay().toMillis)
            })
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ViewState.Error -> showError(context.applicationContext, state.error.message)
            is ViewState.Success -> onBack()
            else -> {}
        }
    }

    if (uiState is ViewState.Loading) ModalLoadingDialog(
        dialogSate = mutableStateOf(true),
        text = context.getString(R.string.loading)
    )


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            val elevation by animateDpAsState(targetValue = if (scrollState.value > 0) 4.dp else 0.dp)
            EditProfileTopAppBar(
                elevation,
                isChanged = isChanged,
                onReset = { resetDialog = true },
                onBack = onBack
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween.also { Arrangement.spacedBy(12.dp) }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {

                Spacer(modifier = Modifier.size(16.dp))
                EditUserPhoto(
                    modifier = Modifier.fillMaxWidth(),
                    currentUser = currentUser,
                    hintText = stringResource(id = R.string.user_photo)
                )


                EditProfileTextFieldWithHeader(
                    modifier = Modifier.fillMaxWidth(),
                    value = currentUser.userName,
                    onValueChange = viewModel::onNameChange,
                    hintText = stringResource(id = R.string.name_hint)
                )

                EditProfileTextFieldWithHeader(
                    modifier = Modifier.fillMaxWidth(),
                    value = currentUser.email,
                    onValueChange = {},
                    hintText = stringResource(id = R.string.email_hint),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {}),
                    icon = Icons.Default.Email,
                    readOnly = true,
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GrayText(text = stringResource(id = R.string.birthday_hint))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Icon(
                            Icons.Default.EditCalendar,
                            Icons.Default.EditCalendar.name
                        )
                        if (currentUser.birthday == 0L) {
                            Text(
                                modifier = Modifier.clickable {
                                    datePickerShown = true
                                },
                                text = stringResource(id = R.string.birthday_set),
                                color = MaterialTheme.customColors.secondaryTextColor
                            )
                        } else {
                            Text(
                                modifier = Modifier.clickable {
                                    datePickerShown = true
                                },
                                text = currentUser.birthday.toDate(),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }


            AnimatedVisibility(visible = isChanged) {
                DefaultButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    text = stringResource(id = R.string.save),
                    enabled = true,
                    onClick = viewModel::updateProfile
                )
            }
        }
    }
}

@Composable
fun EditUserPhoto(modifier: Modifier = Modifier, currentUser: User, hintText: String) {
    Column(modifier = Modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GrayText(text = hintText)
        UserImage(
            modifier = modifier,
            user = currentUser
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResetDialog(onDismiss: () -> Unit, onReset: () -> Unit) {
    DefaultDialog(
        primaryText = stringResource(id = R.string.reset_dialog),
        secondaryText = stringResource(id = R.string.reset_dialog_secondary),
        positiveButtonText = stringResource(id = R.string.yes),
        negativeButtonText = stringResource(id = R.string.no),
        onPositiveClick = { onReset(); onDismiss() },
        onNegativeClick = onDismiss,
        onDismiss = onDismiss
    )
}

@Composable
fun EditProfileTextFieldWithHeader(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hintText: String,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    icon: ImageVector? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GrayText(text = hintText)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            icon?.let { Icon(icon, icon.name) }
            BasicTextField(modifier = modifier, value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp
                ),
                readOnly = readOnly,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
                decorationBox = {
                    Column {
                        it()
                        if (!readOnly) Divider(modifier = Modifier.fillMaxWidth())
                    }
                })
        }
    }

}

@Composable
fun EditProfileTopAppBar(
    elevation: Dp,
    isChanged: Boolean,
    onReset: () -> Unit,
    onBack: () -> Unit
) {
    ScheduleAppBar(
        title = stringResource(id = R.string.profile_edit),
        backClick = onBack,
        elevation = elevation,
        actions = {
            AnimatedVisibility(isChanged) {
                IconButton(onClick = onReset) {
                    Icon(Icons.Default.RestartAlt, Icons.Default.RestartAlt.name)
                }
            }
        })
}
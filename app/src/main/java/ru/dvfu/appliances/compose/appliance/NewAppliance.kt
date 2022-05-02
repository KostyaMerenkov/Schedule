package ru.dvfu.appliances.compose.appliance

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.ScheduleAppBar
import ru.dvfu.appliances.compose.SubtitleWithIcon
import ru.dvfu.appliances.compose.components.ColorPicker
import ru.dvfu.appliances.compose.components.UiState
import ru.dvfu.appliances.compose.ui.theme.pickerColors
import ru.dvfu.appliances.compose.viewmodels.NewApplianceViewModel
import ru.dvfu.appliances.compose.views.ModalLoadingDialog
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.ViewState

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterialApi
@Composable
fun NewAppliance(backPressed: () -> Unit) {
    val viewModel: NewApplianceViewModel = get()
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current

    val (selectedColor, onColorSelected) = remember { mutableStateOf(pickerColors[0]) }
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState) {
        if (uiState is UiState.Success) { backPressed() }
    }

    if (uiState is UiState.InProgress) { ModalLoadingDialog() }

    Scaffold(
        topBar = { ScheduleAppBar(title = "Новое устройство", backClick = backPressed) },
        floatingActionButton = {
            NewApplianceFab {
                if (!viewModel.createNewAppliance())
                    Toast.makeText(
                        context.applicationContext,
                        "Название не заполнено!",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        },
    ) {
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(state = scrollState, enabled = true),
        ) {
            ApplianceNameSet(viewModel.title, viewModel.description)

            Column {
                Text(
                    stringResource(id = R.string.pick_color),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(12.dp)
                )
                Divider(thickness = 1.dp, color = MaterialTheme.colors.onPrimary)
                ColorPicker(
                    pickerColors, selectedColor,
                    onColorSelected.apply {
                        viewModel.selectedColor.value = selectedColor
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun NewApplianceFab(
    onFabClicked: () -> Unit
) {
    FloatingActionButton(
        modifier = Modifier.animateContentSize(), onClick = onFabClicked,
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = stringResource(R.string.add_new_appliance),
        )
    }
}

@Composable
fun ApplianceNameSet(titleState: MutableState<String>, descriptionState: MutableState<String>) {

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SubtitleWithIcon(Modifier, Icons.Default.Menu, "Устройство")
        OutlinedTextField(
            singleLine = true,
            value = titleState.value,
            onValueChange = {
                titleState.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {},
            label = { Text(text = stringResource(id = R.string.main_name)) },
            isError = titleState.value.isEmpty()
        )
        OutlinedTextField(
            singleLine = false,
            value = descriptionState.value,
            onValueChange = {
                descriptionState.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {},
            label = { Text(text = "Описание") },
        )
    }

}

@Composable
fun ErrorDialog(errorDialog: MutableState<Boolean>) {
    val viewModel: NewApplianceViewModel = get()
    val context = LocalContext.current
    AlertDialog(
        title = { Text("Произошла ошибка!") },
        text = { Text("Проверьте интернет соединение и попробуйте еще раз.") },
        onDismissRequest = { errorDialog.value = false },
        confirmButton = {
            OutlinedButton(
                onClick = { viewModel.createNewAppliance() },
                content = { Text(stringResource(R.string.try_again)) })
        }, dismissButton = {
            OutlinedButton(
                onClick = { errorDialog.value = false },
                content = { Text(stringResource(R.string.cancel)) })
        }
    )
}



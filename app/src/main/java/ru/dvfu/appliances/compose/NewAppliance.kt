package ru.dvfu.appliances.compose

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.components.ColorPicker
import ru.dvfu.appliances.compose.ui.theme.pickerColors
import ru.dvfu.appliances.compose.viewmodels.NewApplianceViewModel
import ru.dvfu.appliances.ui.BaseViewState

@ExperimentalMaterialApi
@Composable
fun NewAppliance(backPressed: () -> Unit) {
    val viewModel: NewApplianceViewModel = get()

    val (selectedColor, onColorSelected) = remember { mutableStateOf(pickerColors[0]) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    SubscribeToProgress(viewModel.uiState, backPressed)

    BottomSheetScaffold(

        topBar = { ScheduleAppBar(title = "Новое устройство", backClick = backPressed) },
        floatingActionButton = {
            NewApplianceFab(scaffoldState) {
                if (!viewModel.createNewAppliance())
                    Toast.makeText(context, "Название не заполнено!", Toast.LENGTH_SHORT).show()
            }
        },
        sheetContent = {
            Column {
                Text(
                    "Select color",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(12.dp)
                )
                Divider(thickness = 1.dp, color = MaterialTheme.colors.onPrimary)
                ColorPicker(
                    pickerColors,
                    selectedColor,
                    onColorSelected.apply {
                        viewModel.selectedColor.value = selectedColor
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
    ){
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(state = scrollState, enabled = true),
        ) {
            ApplianceName(viewModel.title, viewModel.description)
            OutlinedButton(onClick = { coroutineScope.launch {
                scaffoldState.bottomSheetState.expand()
            } }, colors = ButtonDefaults.buttonColors(backgroundColor = selectedColor ?: Color.White))
            { Text(stringResource(R.string.choose_color))}
            //ApplianceDescription()
        }
    }
}

@Composable
fun SubscribeToProgress(vmuiState: StateFlow<BaseViewState>, upPress: () -> Unit) {
    val errorDialog = rememberSaveable { mutableStateOf(false) }

    val uiState by vmuiState.collectAsState()
    when (uiState) {
        is BaseViewState.Success<*> -> {
            if ((uiState as BaseViewState.Success<*>).data != null) {
                Toast.makeText(
                    LocalContext.current,
                    "Оборудование добавлено!",
                    Toast.LENGTH_SHORT
                ).show()
                upPress()
            }
        }
        is BaseViewState.Loading -> {
            //LoadingDialog()
        }
        is BaseViewState.Error -> {
            ErrorDialog(errorDialog)
            errorDialog.value = true
            Toast.makeText(
                LocalContext.current,
                "Error: ${(uiState as BaseViewState.Error).error.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun NewApplianceFab(scaffoldState: BottomSheetScaffoldState, onFabClicked: () -> Unit) {

    val defaultBottomPadding: Dp = 128.dp //194
    val paddingBottom = remember { mutableStateOf(defaultBottomPadding) } //128
    val paddingTop = remember { mutableStateOf(0.dp) }

    when (scaffoldState.bottomSheetState.currentValue) {
        BottomSheetValue.Collapsed -> {
            paddingBottom.value = defaultBottomPadding
            paddingTop.value = 0.dp
        }
        BottomSheetValue.Expanded -> {
            paddingBottom.value = 24.dp
            paddingTop.value = 24.dp
        }
    }

    FloatingActionButton(
        modifier = Modifier
            .animateContentSize()
            .padding(bottom = paddingBottom.value, top = paddingTop.value),
        onClick = onFabClicked,
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = "Add new appliance",
            tint = if (true/*viewmodel.allFieldsFilled*/) Color.Gray else LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
        )
    }
}

@Composable
fun ApplianceName(titleState: MutableState<String>, descriptionState: MutableState<String>) {

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
                label = { Text(text = "Название") },
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



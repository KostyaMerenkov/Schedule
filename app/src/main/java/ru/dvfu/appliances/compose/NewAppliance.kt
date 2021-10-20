package ru.dvfu.appliances.compose

import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.dvfu.appliances.R

@ExperimentalMaterialApi
@Composable
fun NewAppliance() {

    val colors = listOf(
        Color(0xFFEF5350),
        Color(0xFFEC407A),
        Color(0xFFAB47BC)
    )
    val (selectedColor, onColorSelected) = remember { mutableStateOf(colors[0]) }

    BottomSheetScaffold (
        sheetContent = {
        Column {
            Text(
                "Select color",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(12.dp)
            )
            Divider(thickness = 1.dp, color = MaterialTheme.colors.onPrimary)
            ColorPicker(
                colors,
                selectedColor,
                onColorSelected as (Color?) -> Unit,
                modifier = Modifier.padding(12.dp)
            )
        }
    },
        scaffoldState = rememberBottomSheetScaffoldState(),
        sheetPeekHeight = 0.dp){
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(state = scrollState, enabled = true),
        ) {
            ApplianceName()

        }
    }
}

@Composable
fun ApplianceName() {
    var textFieldValue by rememberSaveable { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SubtitleWithIcon(Modifier, Icons.Default.Menu, "Устройство")
        OutlinedTextField(
                singleLine = true,
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {},
                label = { Text(text = "Название") },
        )
    }

}

@Composable
fun ColorPicker(
    items: List<Color?>,
    selectedColor: Color?,
    onColorSelected: (color: Color?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyRow(
            contentPadding = PaddingValues(4.dp),
//            mainAxisAlignment = MainAxisAlignment.Start,
//            mainAxisSize = Size.Wrap,
            /*crossAxisSpacing = 4.dp,
            mainAxisSpacing = 4.dp*/
        ) {
            items.distinct().forEach { color ->
                ColorItem(
                    selected = color == selectedColor,
                    color = color,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
fun ColorItem(
    selected: Boolean,
    color: Color?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(CircleShape)
            .requiredSize(40.dp)
            .clickable(onClick = onClick)
    ) {
        if (color != null) {
            // Transparent background pattern
            Box(modifier = Modifier.width(20.dp).fillMaxHeight().background(grey400))
            // Color indicator
            val colorModifier =
                if (color.luminance() < 0.1 || color.luminance() > 0.9) {
                    Modifier.fillMaxSize().background(color).border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface,
                        shape = CircleShape
                    )
                } else {
                    Modifier.fillMaxSize().background(color)
                }
            Box(
                modifier = colorModifier
            ) {
                if (selected) {
                    Icon(
                        Icons.Default.Check,
                        tint = if (color.luminance() < 0.5) Color.White else Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        } else {
            if (selected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .background(if (isSystemInDarkTheme()) whiteAlpha20 else blackAlpha20)
                )
            }
            // Color null indicator
            Icon(
                painterResource(R.drawable.ic_color_off_24dp),
                "",
                modifier = Modifier.align(Alignment.Center),
                tint = contentColorFor(MaterialTheme.colors.surface)
            )
        }
    }
}

val grey400 = Color(0xFFBDBDBD)
val blackAlpha20 = Color(0x33000000)
val whiteAlpha20 = Color(0x33FFFFFF)

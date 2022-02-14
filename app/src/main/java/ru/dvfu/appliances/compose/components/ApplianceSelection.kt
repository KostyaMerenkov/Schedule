package ru.dvfu.appliances.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.dvfu.appliances.compose.home.ItemApplianceSelectable
import ru.dvfu.appliances.model.repository.entity.Appliance

@OptIn(ExperimentalComposeUiApi::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class
)
@Composable
fun ApplianceSelection(
    modifier: Modifier = Modifier,
    radioOptions: List<Appliance>,
    currentOption: State<Appliance?>,
    onSelectedItem: (Appliance) -> Unit,
) {

    // val selected = (currentOption.value == selectedOption)
    //val borderModifier = if (selected) Modifier.border(2.dp, MaterialTheme.colors.primary) else Modifier

    LazyRow(modifier = modifier,
    contentPadding = PaddingValues(4.dp)) {
        items(radioOptions) { item ->

            ItemApplianceSelectable(appliance = item, isSelected = currentOption.value?.id == item.id) {
                onSelectedItem(item)
            }
        }
    }
}


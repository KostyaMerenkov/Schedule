package ru.dvfu.appliances.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.dvfu.appliances.R
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.isAdmin

@Composable
fun FabWithMenu(
    modifier: Modifier = Modifier,
    fabState: MutableState<MultiFabState>,
    currentUser: User,
    onAddEventClick: () -> Unit,
) {
    //val toState = remember { mutableStateOf(MultiFabState.COLLAPSED) }
    val transition = updateTransition(targetState = fabState, label = "")

    val size = transition.animateDp(label = "") { state ->
        if (state.value == MultiFabState.EXPANDED) 48.dp else 0.dp
    }
    val rotation = transition.animateFloat(label = "") { state ->
        if (state.value == MultiFabState.EXPANDED) 45f else 0f
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {

        if (currentUser.isAdmin) {
            FabMenu(item = FabMenuItem(
                icon = Icons.Default.AddTask,
                text = stringResource(id = R.string.new_event),
                onClick = onAddEventClick
            ), size = size.value)
        }


        FloatingActionButton(backgroundColor = Color(0xFFFF8C00),
            onClick = {
            if (transition.currentState.value == MultiFabState.EXPANDED) {
                transition.currentState.value = MultiFabState.COLLAPSED
            } else transition.currentState.value = MultiFabState.EXPANDED
        }) {
            Icon(
                imageVector = Icons.Default.Add,
                modifier = Modifier.rotate(rotation.value),
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = ""
            )
        }
    }
}

@Composable
fun FabMenu(item: FabMenuItem, modifier: Modifier = Modifier, size: Dp) {
    AnimatedVisibility(
        size != 0.dp,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            Text(item.text, color = Color.White)

            Box(modifier = Modifier
                .size(FabSize)
                .padding((FabSize - size) / 2)) {
                FloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    modifier = modifier.size(size),
                    onClick = item.onClick
                ) {
                    Icon(
                        tint = MaterialTheme.colors.onPrimary,
                        imageVector = item.icon,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

private val FabSize = 56.dp


class FabMenuItem(
    val icon: ImageVector,
    val text: String = "",
    val onClick: () -> Unit
)

enum class MultiFabState {
    COLLAPSED, EXPANDED
}
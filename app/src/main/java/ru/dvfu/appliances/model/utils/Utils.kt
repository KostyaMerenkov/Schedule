package ru.dvfu.appliances.model.utils

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import java.util.*

fun randomUUID() = UUID.randomUUID().toString()

fun Modifier.loadingModifier(
    enabled: Boolean = true,
) = composed(inspectorInfo = debugInspectorInfo {
    name = "loadingModifier"
    value = enabled
}) {
    if (enabled)
        Modifier.placeholder(
            true,
            color = Color.LightGray,
            shape = CircleShape,
            highlight = PlaceholderHighlight.shimmer()
        ) else Modifier
}
package ru.dvfu.appliances.model.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import ru.dvfu.appliances.R
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

fun showError(applicationContext: Context, text: String?) {
    showToast(applicationContext.applicationContext,
        text ?: applicationContext.resources.getString(R.string.error_occured))
}

fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}
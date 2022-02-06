package ru.dvfu.appliances.compose.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.dvfu.appliances.compose.ui.theme.customColors

@Composable
fun HeaderText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    textColor: Color = MaterialTheme.colors.onSurface
) {
    Text(
        modifier = modifier.padding(8.dp),
        style = MaterialTheme.typography.h6,
        textAlign = textAlign,
        color = textColor,
        text = text
    )
}

@Composable
fun HeaderTextSecondary(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    HeaderText(modifier, text, textAlign, MaterialTheme.customColors.secondaryTextColor)
}

@Composable
fun PrimaryText(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    text: String,
    textColor: Color = MaterialTheme.colors.onSurface,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.h4,
        fontSize = 18.sp,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = textColor,
        text = text,
        maxLines = maxLines,
        softWrap = true,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun PrimaryTextSmall(
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    text: String,
    maxLines: Int = Int.MAX_VALUE,
    textColor: Color = MaterialTheme.colors.onSurface
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.h4,
        fontSize = 14.sp,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        color = textColor,
        text = text
    )
}

@Composable
fun PrimaryTextBold(modifier: Modifier = Modifier, text: String) {
    PrimaryText(
        modifier = modifier,
        fontWeight = FontWeight.SemiBold,
        text = text,
        maxLines = 1
    )
}

@Composable
fun DefaultButtonFilled(
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        enabled = enabled,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.primaryVariant),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primaryVariant,
            contentColor = MaterialTheme.colors.onPrimary
        ),
        onClick = onClick
    ) {
        icon?.let {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 4.dp),
                painter = it,
                contentDescription = null,
            )
        }
        Text(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            text = text.replaceFirstChar { it.uppercase() },
            maxLines = 1
        )
    }
}

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick
    ) {
        icon?.let {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 4.dp),
                painter = it,
                contentDescription = null,
                tint = MaterialTheme.colors.primaryVariant
            )
        }
        Text(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            text = text.replaceFirstChar { it.uppercase() },
            color = MaterialTheme.colors.primaryVariant,
            maxLines = 1
        )
    }
}
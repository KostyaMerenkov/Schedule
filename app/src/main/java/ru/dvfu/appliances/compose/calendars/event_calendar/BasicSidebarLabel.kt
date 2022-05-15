package ru.dvfu.appliances.compose.calendars.event_calendar

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalTime


@Composable
fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    val currentTime = remember { LocalTime.now() }
    Text(
        text = time.format(HourFormatter),
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp),
        color = if (time.hour == currentTime.hour) MaterialTheme.colors.primary else Color.Unspecified,
        fontWeight = if (time.hour == currentTime.hour) FontWeight.ExtraBold else null
    )
}

@Preview(showBackground = true)
@Composable
fun BasicSidebarLabelPreview() {
    BasicSidebarLabel(time = LocalTime.of(13, 0), Modifier.sizeIn(maxHeight = 64.dp))
}
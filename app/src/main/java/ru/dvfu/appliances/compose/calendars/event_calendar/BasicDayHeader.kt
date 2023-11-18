package ru.dvfu.appliances.compose.calendars.event_calendar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime


@Composable
fun BasicDayHeader(
    day: LocalDate,
    modifier: Modifier = Modifier,
) {
    val currentDate = remember { LocalDate.now() }
    Text(
        text = day.format(DayFormatter),
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        fontWeight = if (day.dayOfMonth == currentDate.dayOfMonth) FontWeight.ExtraBold else null
    )
}

@Preview(showBackground = true)
@Composable
fun BasicDayHeaderPreview() {
        BasicDayHeader(day = LocalDate.now())
}
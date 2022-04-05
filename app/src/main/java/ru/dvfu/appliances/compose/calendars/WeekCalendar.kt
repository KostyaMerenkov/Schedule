package ru.dvfu.appliances.compose.calendars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.himanshoe.kalendar.ui.Kalendar
import com.himanshoe.kalendar.ui.KalendarType
import ru.dvfu.appliances.compose.event_calendar.CalendarEvent
import java.time.LocalDate

@Composable
fun WeekCalendar(events: List<CalendarEvent>, navController: NavController, onDaySelected: (LocalDate) -> Unit) {

    Scaffold {
        Column {
            Kalendar(kalendarType = KalendarType.Oceanic(), onCurrentDayClick = { day, event ->

            }, errorMessage = {
                //Handle the error if any
            })
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                items(events) { event ->
                    Row {
                        Text(text = event.applianceId)
                    }
                }
            }

        }
    }
}
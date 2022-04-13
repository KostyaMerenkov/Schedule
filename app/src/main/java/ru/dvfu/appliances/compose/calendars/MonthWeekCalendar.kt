package ru.dvfu.appliances.compose.calendars

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.himanshoe.kalendar.common.KalendarSelector
import com.himanshoe.kalendar.common.KalendarStyle
import com.himanshoe.kalendar.common.data.KalendarEvent
import com.himanshoe.kalendar.ui.Kalendar
import com.himanshoe.kalendar.ui.KalendarType
import ru.dvfu.appliances.compose.appliance.LoadingItem
import ru.dvfu.appliances.compose.appliance.NoElementsView
import ru.dvfu.appliances.compose.event_calendar.CalendarEvent
import ru.dvfu.appliances.compose.event_calendar.EventTimeFormatter
import ru.dvfu.appliances.compose.viewmodels.EventsState
import ru.dvfu.appliances.compose.viewmodels.WeekCalendarViewModel
import ru.dvfu.appliances.model.utils.loadingModifier
import java.time.LocalDateTime

@Composable
fun MonthWeekCalendar(
    viewModel: WeekCalendarViewModel,
    calendarType: KalendarType = KalendarType.Oceanic(),
    onEventClick: (CalendarEvent) -> Unit,
    onEventLongClick: (CalendarEvent) -> Unit,
) {
    val currentDate = viewModel.currentDate.collectAsState()
    val dayEvents = viewModel.dayEvents
    Scaffold {
        Column {
            Kalendar(kalendarType = calendarType, onCurrentDayClick = { day, event ->
                viewModel.onDaySelected(day)

            }, errorMessage = {
                //Handle the error if any
            },
                kalendarStyle = KalendarStyle(kalendarSelector = KalendarSelector.Circle()),
                kalendarEvents = dayEvents.filter { (it.value as? EventsState.Loaded)?.events?.isEmpty() == false }
                    .map { KalendarEvent(it.key, "") }
            )
            LazyColumn(contentPadding = PaddingValues(8.dp)) {
                dayEvents[currentDate.value]?.let {
                    when (it) {
                        is EventsState.Loaded -> {
                            if (it.events.isEmpty()) {
                                item {
                                    NoElementsView(mainText = "Нет событий на выбранный день") {}
                                }
                            }
                            items(it.events) { event ->
                                EventView(
                                    onEventClick = onEventClick,
                                    onEventLongClick = onEventLongClick,
                                    event = event
                                )
                            }
                        }
                        EventsState.Loading -> {
                            items(2) {
                                EventView(
                                    childModifier = Modifier.loadingModifier(),
                                    event = CalendarEvent(
                                        applianceName = "Appliance",
                                        start = LocalDateTime.now(),
                                        end = LocalDateTime.now()
                                    ), onEventClick = {}, onEventLongClick = {})
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventView(
    modifier: Modifier = Modifier,
    childModifier: Modifier = Modifier,
    onEventClick: (CalendarEvent) -> Unit,
    onEventLongClick: (CalendarEvent) -> Unit,
    event: CalendarEvent
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .clipToBounds()
            .background(
                event.color,
                shape = RoundedCornerShape(4.dp)
            )
            .combinedClickable(
                onClick = { onEventClick(event) },
                onLongClick = { onEventLongClick(event) }
            )
            .then(childModifier)
    ) {
        Column(Modifier.padding(4.dp)) {
            Text(
                text = "${event.start.format(EventTimeFormatter)} - ${
                    event.end.format(
                        EventTimeFormatter
                    )
                }",
                style = MaterialTheme.typography.caption,
                maxLines = 2,
                overflow = TextOverflow.Clip,
                modifier = childModifier
            )

            Text(
                text = event.applianceName,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = childModifier
            )

            if (event.description.isNotBlank()) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.body2,

                    overflow = TextOverflow.Ellipsis,
                    modifier = childModifier
                )
            }
        }

    }
}

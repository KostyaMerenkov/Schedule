package ru.dvfu.appliances.compose.calendars

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.himanshoe.kalendar.common.KalendarKonfig
import com.himanshoe.kalendar.common.KalendarSelector
import com.himanshoe.kalendar.common.KalendarStyle
import com.himanshoe.kalendar.common.data.KalendarEvent
import com.himanshoe.kalendar.ui.Kalendar
import com.himanshoe.kalendar.ui.KalendarType
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.getViewModel
import ru.dvfu.appliances.compose.appliance.LoadingItem
import ru.dvfu.appliances.compose.appliance.NoElementsView
import ru.dvfu.appliances.compose.event_calendar.CalendarEvent
import ru.dvfu.appliances.compose.event_calendar.EventTimeFormatter
import ru.dvfu.appliances.compose.event_calendar.SplitType
import ru.dvfu.appliances.compose.home.EventOptionDialog
import ru.dvfu.appliances.compose.viewmodels.EventsState
import ru.dvfu.appliances.compose.viewmodels.WeekCalendarViewModel
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.utils.toLocalDateTime
import java.time.LocalDate

@Composable
fun WeekCalendar(
    viewModel: WeekCalendarViewModel,
    onEventClick: (CalendarEvent) -> Unit,
    onEventLongClick: (CalendarEvent) -> Unit,
) {
    val currentDate = viewModel.currentDate.collectAsState()
    val dayEvents = viewModel.dayEvents
    Scaffold {
        Column {
            Kalendar(kalendarType = KalendarType.Oceanic(), onCurrentDayClick = { day, event ->
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
                                EventView(event = event, onEventClick = onEventClick, onEventLongClick = onEventLongClick)
                            }
                        }
                        EventsState.Loading -> {
                            item {
                                LoadingItem(modifier = Modifier.fillMaxWidth())
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
    event: CalendarEvent,
    onEventClick: (CalendarEvent) -> Unit,
    onEventLongClick: (CalendarEvent) -> Unit
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
            )

            Text(
                text = event.applianceName,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (event.description.isNotBlank()) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.body2,

                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

    }
}

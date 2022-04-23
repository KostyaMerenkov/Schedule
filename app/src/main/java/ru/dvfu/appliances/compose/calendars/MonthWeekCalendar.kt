package ru.dvfu.appliances.compose.calendars

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.himanshoe.kalendar.common.KalendarSelector
import com.himanshoe.kalendar.common.KalendarStyle
import com.himanshoe.kalendar.common.data.KalendarEvent
import com.himanshoe.kalendar.ui.Kalendar
import com.himanshoe.kalendar.ui.KalendarType
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.appliance.NoElementsView
import ru.dvfu.appliances.compose.event_calendar.EventTimeFormatter
import ru.dvfu.appliances.compose.viewmodels.EventsState
import ru.dvfu.appliances.compose.viewmodels.WeekCalendarViewModel
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.utils.Constants
import ru.dvfu.appliances.model.utils.loadingModifier
import java.time.LocalDate
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
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .verticalScroll(scrollState)) {
        Kalendar(kalendarType = calendarType, onCurrentDayClick = { day, event ->
            viewModel.onDaySelected(day)

        }, errorMessage = {
            //Handle the error if any
        },
            selectedDay = currentDate.value,
            kalendarStyle = KalendarStyle(kalendarSelector = KalendarSelector.Circle()),
            kalendarEvents = dayEvents.filter { (it.value as? EventsState.Loaded)?.events?.isEmpty() == false }
                .map { KalendarEvent(it.key, "") }
        )
        Column(modifier = Modifier.padding(8.dp).padding(bottom = 150.dp)) {
        dayEvents[currentDate.value]?.let {
            when (it) {
                is EventsState.Loaded -> {
                    if (it.events.isEmpty()) {
                        NoElementsView(mainText = "Нет событий на выбранный день") {}
                    }
                    it.events.forEach { event ->
                        EventView(
                            onEventClick = onEventClick,
                            onEventLongClick = onEventLongClick,
                            event = event
                        )
                    }
                }
                EventsState.Loading -> {
                    (0..2).forEach {
                        EventView(
                            childModifier = Modifier.loadingModifier(),
                            event = CalendarEvent(
                                appliance = Appliance(name = "Appliance"),
                                date = LocalDate.now(),
                                timeCreated = LocalDateTime.now(),
                                timeStart = LocalDateTime.now(),
                                timeEnd = LocalDateTime.now()
                            ), onEventClick = {}, onEventLongClick = {})
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
                event.appliance?.color?.let { Color(it) } ?: Constants.DEFAULT_EVENT_COLOR,
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
                text = "${event.timeStart.format(EventTimeFormatter)} - ${
                    event.timeEnd.format(EventTimeFormatter)
                }",
                style = MaterialTheme.typography.caption,
                maxLines = 2,
                overflow = TextOverflow.Clip,
                modifier = childModifier
            )

            Text(
                text = event.appliance?.name ?: stringResource(id = R.string.appliance_name_failed),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = childModifier
            )

            if (event.commentary.isNotBlank()) {
                Text(
                    text = event.commentary,
                    style = MaterialTheme.typography.body2,

                    overflow = TextOverflow.Ellipsis,
                    modifier = childModifier
                )
            }
        }

    }
}

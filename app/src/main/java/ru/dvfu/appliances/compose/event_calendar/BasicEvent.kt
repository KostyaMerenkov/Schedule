package ru.dvfu.appliances.compose.event_calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BasicEvent(
    positionedEvent: PositionedEvent,
    modifier: Modifier = Modifier,
    onEventClick: (CalendarEvent) -> Unit,
    onEventLongClick: (CalendarEvent) -> Unit,
) {
    val event = positionedEvent.calendarEvent
    val topRadius = if (positionedEvent.splitType == SplitType.Start || positionedEvent.splitType == SplitType.Both) 0.dp else 4.dp
    val bottomRadius = if (positionedEvent.splitType == SplitType.End || positionedEvent.splitType == SplitType.Both) 0.dp else 4.dp
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = 2.dp,
                end = 2.dp,
                bottom = if (positionedEvent.splitType == SplitType.End) 0.dp else 2.dp
            )
            .clipToBounds()
            .background(
                event.color,
                shape = RoundedCornerShape(
                    topStart = topRadius,
                    topEnd = topRadius,
                    bottomEnd = bottomRadius,
                    bottomStart = bottomRadius,
                )
            )
            .combinedClickable(
                onClick = { onEventClick(event) },
                onLongClick = { onEventLongClick(event) }
            )
            .padding(4.dp)
    ) {
        Text(
            text = "${event.start.format(EventTimeFormatter)} - ${event.end.format(EventTimeFormatter)}",
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

        if (event.description != null) {
            Text(
                text = event.description,
                style = MaterialTheme.typography.body2,

                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

val sampleEvents = listOf(
    CalendarEvent(
        applianceName = "Google I/O Keynote",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-05-18T09:00:00"),
        end = LocalDateTime.parse("2021-05-18T11:00:00"),
        description = "Tune in to find out about how we're furthering our mission to organize the world’s information and make it universally accessible and useful.",
    ),
    /*CalendarEvent(
        name = "Developer Keynote",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-05-18T09:00:00"),
        end = LocalDateTime.parse("2021-05-18T15:00:00"),
        description = "Learn about the latest updates to our developer products and platforms from Google Developers.",
    ),
    CalendarEvent(
        name = "What's new in Android",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-18T10:00:00"),
        end = LocalDateTime.parse("2021-05-18T11:30:00"),
        description = "In this Keynote, Chet Haase, Dan Sandler, and Romain Guy discuss the latest Android features and enhancements for developers.",
    ),
    CalendarEvent(
        name = "What's new in Material Design",
        color = Color(0xFF6DD3CE),
        start = LocalDateTime.parse("2021-05-18T11:00:00"),
        end = LocalDateTime.parse("2021-05-18T11:45:00"),
        description = "Learn about the latest design improvements to help you build personal dynamic experiences with Material Design.",
    ),*/
    CalendarEvent(
        applianceName = "What's new in Machine Learning",
        color = Color(0xFFF4BFDB),
        start = LocalDateTime.parse("2021-05-18T10:00:00"),
        end = LocalDateTime.parse("2021-05-18T12:00:00"),
        description = "Learn about the latest and greatest in ML from Google. We’ll cover what’s available to developers when it comes to creating, understanding, and deploying models for a variety of different applications.",
    ),
    CalendarEvent(
        applianceName = "What's new in Machine Learning",
        color = Color(0xFFF4BFDB),
        start = LocalDateTime.parse("2021-05-18T10:30:00"),
        end = LocalDateTime.parse("2021-05-18T11:30:00"),
        description = "Learn about the latest and greatest in ML from Google. We’ll cover what’s available to developers when it comes to creating, understanding, and deploying models for a variety of different applications.",
    ),
    CalendarEvent(
        applianceName = "Jetpack Compose Basics",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-20T12:00:00"),
        end = LocalDateTime.parse("2021-05-20T13:00:00"),
        description = "This Workshop will take you through the basics of building your first app with Jetpack Compose, Android's new modern UI toolkit that simplifies and accelerates UI development on Android.",
    ),
)

class EventsProvider : PreviewParameterProvider<CalendarEvent> {
    override val values = sampleEvents.asSequence()
}

@Preview(showBackground = true)
@Composable
fun EventPreview(
    @PreviewParameter(EventsProvider::class) calendarEvent: CalendarEvent,
) {
        BasicEvent(
            PositionedEvent(calendarEvent, SplitType.None, calendarEvent.start.toLocalDate(), calendarEvent.start.toLocalTime(), calendarEvent.end.toLocalTime()),
            modifier = Modifier.sizeIn(maxHeight = 64.dp),
            onEventClick = {},
            onEventLongClick = {}
        )
}
package ru.dvfu.appliances.compose.event_calendar

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import java.time.format.DateTimeFormatter

private class EventDataModifier(
    val positionedEvent: PositionedEvent,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = positionedEvent
}

fun Modifier.eventData(positionedEvent: PositionedEvent) = this.then(EventDataModifier(positionedEvent))


val EventTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
val HourFormatter = DateTimeFormatter.ofPattern("H")
val DayFormatter = DateTimeFormatter.ofPattern("EE, MMM d")
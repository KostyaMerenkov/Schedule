package ru.dvfu.appliances.compose.event_calendar

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import java.time.format.DateTimeFormatter

private class EventDataModifier(
    val event: Event,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}

fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))


public val EventTimeFormatter = DateTimeFormatter.ofPattern("hh:mm")

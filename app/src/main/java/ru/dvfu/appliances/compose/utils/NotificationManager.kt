package ru.dvfu.appliances.compose.utils

import ru.dvfu.appliances.compose.viewmodels.EventDateAndTime
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.Event

interface NotificationManager {

    suspend fun applianceDeleted(appliance: Appliance)
    suspend fun eventUpdated(event: CalendarEvent, data: Map<String, Any?>)
    suspend fun eventDeleted(event: CalendarEvent)
    suspend fun newEvent(newEvent: Event)
    suspend fun newEventStatus(event: CalendarEvent, newStatus: BookingStatus)
    suspend fun eventTimeChanged(event: CalendarEvent, eventDateAndTime: EventDateAndTime)
}
package ru.dvfu.appliances.compose.utils

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.notifications.NotificationData
import ru.dvfu.appliances.model.repository.entity.notifications.PushNotification
import ru.dvfu.appliances.model.repository.entity.notifications.RetrofitInstance
import ru.dvfu.appliances.model.utils.formattedDate
import ru.dvfu.appliances.model.utils.formattedTime

interface NotificationManager {

    suspend fun applianceDeleted(appliance: Appliance)
    suspend fun eventUpdated(event: CalendarEvent, data: Map<String, Any?>)
    suspend fun eventDeleted(event: CalendarEvent)
    suspend fun newEvent(newEvent: Event)
    suspend fun newEventStatus(event: CalendarEvent, newStatus: BookingStatus)
}
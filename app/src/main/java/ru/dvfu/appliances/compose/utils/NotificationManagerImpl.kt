package ru.dvfu.appliances.compose.utils

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.notifications.Notification
import ru.dvfu.appliances.model.repository.entity.notifications.NotificationData
import ru.dvfu.appliances.model.repository.entity.notifications.PushNotification
import ru.dvfu.appliances.model.repository.entity.notifications.RetrofitInstance
import ru.dvfu.appliances.model.utils.formattedDate
import ru.dvfu.appliances.model.utils.formattedTime

class NotificationManagerImpl(
    private val userDatastore: UserDatastore,
    private val usersRepository: UsersRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getApplianceUseCase: GetApplianceUseCase,
) : NotificationManager {

    override suspend fun applianceDeleted(appliance: Appliance) {
        //if (userDatastore.getCurrentUser.first().userId != event.user.userId)
        val users = (appliance.userIds + appliance.superuserIds).mapNotNull {
            getUserUseCase(it).first().getOrNull()?.msgToken
        }

        users.forEach {
            sendMessage(
                PushNotification(
                    to = it,
                    notification = Notification(
                        title = "Прибор \"${appliance.name}\" был удален",
                        body = ""
                    )
                )
            )
        }
    }

    override suspend fun eventUpdated(event: CalendarEvent, data: Map<String, Any?>) {
        //if (userDatastore.getCurrentUser.first().userId != event.user.userId)

        sendMessage(
            PushNotification(
                to = event.user.msgToken,
                notification = Notification(
                    title = "Изменено бронирование на прибор \"${event.appliance.name}\"",
                    body = "${formattedDate(event.date)}, ${
                        formattedTime(event.timeStart, event.timeEnd)
                    }, ${event.status.getName().uppercase()}"
                ),
                data = NotificationData(event.managerCommentary)
            )
        )
    }

    override suspend fun eventDeleted(event: CalendarEvent) {
        sendMessage(
            PushNotification(
                to = event.user.msgToken,
                notification = Notification(
                    title = "Отменено бронирование на прибор \"${event.appliance.name}\"",
                    body = "${formattedDate(event.date)} ${
                        formattedTime(
                            event.timeStart,
                            event.timeEnd
                        )
                    }"
                )
            )
        )
    }

    override suspend fun newEvent(newEvent: Event) {
        val users = usersRepository.getUsers().first()
        getApplianceUseCase(newEvent.applianceId).first().getOrNull()?.let { appliance ->
            val tokens =
                users.filter { appliance.superuserIds.contains(it.userId) }.map { it.msgToken }
            tokens.forEach {
                sendMessage(
                    PushNotification(
                        to = it,
                        notification = Notification(
                            title = "Новое бронирование на прибор ${appliance.name}",
                            body = ""
                        )
                    )
                )

                /*Firebase.messaging.send(
                    RemoteMessage.Builder("$SENDER_ID@gcm.googleapis.com").setData(
                        mapOf(
                            "token" to it,
                            "notification" to bundleOf(
                                "title" to "Breaking News",
                                "body" to "New news story available."
                            ).toString(),
                        )
                    ).build()
                )*/
            }
        }
    }

    override suspend fun newEventStatus(event: CalendarEvent, newStatus: BookingStatus) {
        sendMessage(
            PushNotification(
                to = event.user.msgToken,
                notification = Notification(
                    title = "Ваше бронирование ${newStatus.getName().uppercase()}",
                    body = "\"${event.appliance.name}\", ${formattedDate(event.date)}, ${formattedTime(event.timeStart, event.timeEnd)
                    }",
                ),
                data = NotificationData(event.managerCommentary)
            )
        )
    }

    private suspend fun sendMessage(pushNotification: PushNotification) {
        RetrofitInstance.api.postNotification(pushNotification)
    }

    suspend fun subscribeCurrentUser() {
        val currentUser = userDatastore.getCurrentUser.single()
        if (currentUser.isAnonymousOrGuest()) return

        Firebase.messaging.subscribeToTopic("weather")
            .addOnCompleteListener { task ->
                /*var msg = getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.msg_subscribe_failed)
                }
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()*/
            }
    }
}
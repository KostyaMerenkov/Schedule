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

class NotificationManager(
    private val userDatastore: UserDatastore,
    private val usersRepository: UsersRepository,
    private val getUserUseCase: GetUserUseCase,
    private val appliancesRepository: AppliancesRepository,
    private val getApplianceUseCase: GetApplianceUseCase,
) {

    suspend fun applianceDeleted(appliance: Appliance) {
        //if (userDatastore.getCurrentUser.first().userId != event.user.userId)
        val users = (appliance.userIds + appliance.superuserIds).mapNotNull {
            getUserUseCase(it).first().getOrNull()?.msgToken
        }

        users.forEach {
            sendMessage(
                PushNotification(
                    to = it,
                    notification = NotificationData(
                        title = "Прибор \"${appliance.name}\" был удален",
                        body = ""
                    )
                )
            )
        }
    }

    suspend fun eventUpdated(event: CalendarEvent, data: Map<String, Any?>) {
        //if (userDatastore.getCurrentUser.first().userId != event.user.userId)
        val status = when (event.status) {
            BookingStatus.DECLINED -> "Отклонено"
            BookingStatus.APPROVED -> "Подтверждено"
            BookingStatus.NONE -> "На рассмотрении"
        }

        sendMessage(
            PushNotification(
                to = event.user.msgToken,
                notification = NotificationData(
                    title = "Ваше бронирование на прибор \"${event.appliance.name}\" было изменено",
                    body = "Дата: ${formattedDate(event.date)}\nВремя:${
                        formattedTime(
                            event.timeStart,
                            event.timeEnd
                        )
                    }\nСтатус: $status"
                    // TODO: Добавить комментарий суперпользователя при наличии
                )
            )
        )
    }

    suspend fun eventDeleted(event: CalendarEvent) {
        sendMessage(
            PushNotification(
                to = event.user.msgToken,
                notification = NotificationData(
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

    suspend fun newEvent(newEvent: Event) {
        val users = usersRepository.getUsers().first()
        getApplianceUseCase(newEvent.applianceId).first().getOrNull()?.let { appliance ->
            val tokens =
                users.filter { appliance.superuserIds.contains(it.userId) }.map { it.msgToken }
            tokens.forEach {
                sendMessage(
                    PushNotification(
                        to = it,
                        notification = NotificationData(
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
package ru.dvfu.appliances.compose.utils

import androidx.core.os.bundleOf
import com.google.firebase.inappmessaging.ktx.inAppMessaging
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import org.json.JSONArray
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.notifications.NotificationData
import ru.dvfu.appliances.model.repository.entity.notifications.PushNotification
import ru.dvfu.appliances.model.repository.entity.notifications.RetrofitInstance
import ru.dvfu.appliances.model.utils.Constants.SENDER_ID

class NotificationManager(
    private val userDatastore: UserDatastore,
    private val usersRepository: UsersRepository,
    private val getUserUseCase: GetUserUseCase,
    private val appliancesRepository: AppliancesRepository,
) {

    suspend fun eventDeleted(event: CalendarEvent) {
        /*when(event.status) {
            BookingStatus.APPROVED, BookingStatus.NONE -> {}
        }*/
        event.user?.let { user ->
            sendMessage(
                PushNotification(
                    to = user.msgToken,
                    notification = NotificationData(
                        title = "Удалено бронирование на прибор ${event.appliance?.name ?: ""}",
                        body = ""
                    )
                )
            )
        }
    }

    suspend fun newEvent(newEvent: Event) {
        val users = usersRepository.getUsers().first()
        appliancesRepository.getAppliance(newEvent.applianceId).first().getOrNull()
            ?.let { appliance ->
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
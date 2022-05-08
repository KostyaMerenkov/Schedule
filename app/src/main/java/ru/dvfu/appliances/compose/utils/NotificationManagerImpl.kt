package ru.dvfu.appliances.compose.utils

import androidx.lifecycle.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import ru.dvfu.appliances.BuildConfig
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.compose.viewmodels.EventDateAndTime
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.*
import ru.dvfu.appliances.model.repository.entity.notifications.Notification
import ru.dvfu.appliances.model.repository.entity.notifications.NotificationData
import ru.dvfu.appliances.model.repository.entity.notifications.PushNotification
import ru.dvfu.appliances.model.repository.entity.notifications.RetrofitInstance
import ru.dvfu.appliances.model.utils.*
import ru.dvfu.appliances.model.utils.Constants.NotificationType

class NotificationManagerImpl(
    private val userDatastore: UserDatastore,
    private val usersRepository: UsersRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getApplianceUseCase: GetApplianceUseCase,
) : NotificationManager, LifecycleEventObserver {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    /*val currentUser = MutableStateFlow(User())

    init {
        getCurrentUserListener()
    }

    private fun getCurrentUserListener() {
        scope.launch {
           userDatastore.getCurrentUser.collect {
               currentUser.value = it
           }
        }
    }*/


    override suspend fun applianceDeleted(appliance: Appliance) {
        val currentUser = userDatastore.getCurrentUser.first()
        val users = (appliance.userIds + appliance.superuserIds)
            .mapNotNull { getUserUseCase(it).first().getOrNull() }
            .apply {
                if (BuildConfig.DEBUG.not())
                    filter { it.userId != currentUser.userId }
            }
            .map { it.msgToken }

        users.forEach {
            sendMessage(
                PushNotification(
                    to = it,
                    notification = Notification(
                        title = "Прибор \"${appliance.name}\" был удален",
                        body = "Также были отменены все бронирования на нем"
                    ),
                    data = NotificationData(NotificationType.APPLIANCE)
                )
            )
        }
    }

    override suspend fun eventUpdated(event: CalendarEvent, data: Map<String, Any?>) {
        if (userDatastore.getCurrentUser.first().userId != event.user.userId)

            sendMessage(
                PushNotification(
                    to = event.user.msgToken,
                    notification = Notification(
                        title = "Изменено бронирование на прибор \"${event.appliance.name}\"",
                        body = formattedDateTime(event.date, event.timeStart, event.timeEnd)
                                + ", ${event.status.getName().uppercase()}"
                    ),
                    data = NotificationData(NotificationType.MY_EVENT)
                )
            )
    }

    override suspend fun eventDeleted(event: CalendarEvent) {
        val currentUser = userDatastore.getCurrentUser.first()

        if (BuildConfig.DEBUG || currentUser.userId != event.user.userId) {
            sendMessage(
                PushNotification(
                    to = event.user.msgToken,
                    notification = Notification(
                        title = "Отменено бронирование на прибор \"${event.appliance.name}\"",
                        body = formattedDateTime(event.date, event.timeStart, event.timeEnd)
                    ),
                    data = NotificationData(NotificationType.MY_EVENT)
                )
            )
        }
    }

    override suspend fun newEvent(newEvent: Event) {
        val users = usersRepository.getUsers().first()
        val currentUser = userDatastore.getCurrentUser.first()
        getApplianceUseCase(newEvent.applianceId).first().getOrNull()?.let { appliance ->
            users.filter { appliance.superuserIds.contains(it.userId) }
                .apply { if (BuildConfig.DEBUG.not()) filter { it.userId != currentUser.userId } }
                .map { it.msgToken }
                .forEach {
                    sendMessage(
                        PushNotification(
                            to = it,
                            notification = Notification(
                                title = "Новое бронирование",
                                body = formattedApplianceDateTime(
                                    appliance.name,
                                    newEvent.date.toLocalDate(),
                                    newEvent.timeStart.toLocalDateTime(),
                                    newEvent.timeEnd.toLocalDateTime()
                                )
                            ),
                            data = NotificationData(NotificationType.NEW_EVENT)
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
                    body = formattedApplianceDateTimeStatus(
                        date = event.date,
                        event.timeStart,
                        event.timeEnd,
                        event.appliance.name,
                        status = event.status
                    ),
                ),
                data = NotificationData(NotificationType.MY_EVENT)
            )
        )
    }

    override suspend fun eventTimeChanged(
        event: CalendarEvent,
        eventDateAndTime: EventDateAndTime
    ) {
        val currentUser = userDatastore.getCurrentUser.first()
        val sendTo = mutableListOf<String>()

        if (BuildConfig.DEBUG) {
            sendTo.add(event.user.msgToken)
            event.managedUser?.msgToken?.let { sendTo.add(it) }
        } else {
            if (currentUser.userId != event.user.userId) {
                sendTo.add(event.user.msgToken)
            }
            event.managedUser?.let {
                if (currentUser.userId != it.userId) {
                    sendTo.add(it.msgToken)
                }
            }
        }

        sendTo.forEach {
            sendMessage(
                PushNotification(
                    to = it,
                    notification = Notification(
                        title = "Изменено время бронирования",
                        body = formattedAppliance(event.appliance.name) + ", " +
                                formattedDateTimeStatus(
                                    event.date,
                                    event.timeStart,
                                    event.timeEnd,
                                    event.status
                                )
                    ),
                    data = NotificationData(
                        when (it) {
                            event.user.msgToken -> NotificationType.MY_EVENT
                            else -> NotificationType.EVENT
                        }
                    )
                )
            )
        }
    }

    override suspend fun newUserRole(user: User, role: Roles) {
        sendMessage(
            PushNotification(
                to = user.msgToken,
                notification = Notification(
                    title = "Ваша роль изменена",
                    body = "Теперь вы \"${Firebase.app.applicationContext.getString(role.stringRes)}\""
                ),
                data = NotificationData(NotificationType.DEFAULT)
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

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        /*when(event) {
            Lifecycle.Event.ON_CREATE -> TODO()
            Lifecycle.Event.ON_START -> {
                job.start()
            }
            Lifecycle.Event.ON_RESUME -> TODO()
            Lifecycle.Event.ON_PAUSE -> TODO()
            Lifecycle.Event.ON_STOP -> {
                job.cancel()
            }
            Lifecycle.Event.ON_DESTROY -> TODO()
            Lifecycle.Event.ON_ANY -> TODO()
        }*/
    }
}


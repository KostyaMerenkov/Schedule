package ru.dvfu.appliances

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService
import org.koin.android.ext.android.get
import ru.dvfu.appliances.compose.utils.NotificationType
import ru.dvfu.appliances.model.FirebaseMessagingViewModel
import ru.dvfu.appliances.model.utils.Constants
import ru.dvfu.appliances.model.utils.Constants.NOTIFICATION_CHANNEL_ID
import java.lang.Error
import java.util.*


class MyFirebaseMessagingService() : FirebaseMessagingService() {

    val viewModel: FirebaseMessagingViewModel = get()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.body?.let {
            Log.d("MSG", it)
        }

        remoteMessage.notification?.let {
            val channelId = getNotificationTypeChannel(data = remoteMessage.data)
            showNotification(notification = it, channelId = channelId)
        }
    }

    private fun getNotificationTypeChannel(data: Map<String, String>): String =
        try {
            data.get("notificationType")?.let {
                return when (NotificationType.valueOf(it)) {
                    NotificationType.APPLIANCE -> Constants.APPLIANCE_CHANNEL_ID
                    NotificationType.EVENT -> Constants.EVENT_CHANNEL_ID
                    NotificationType.MY_EVENT -> Constants.MY_EVENT_CHANNEL_ID
                    NotificationType.NEW_EVENT -> Constants.NEW_EVENT_CHANNEL_ID
                    else -> NOTIFICATION_CHANNEL_ID
                }
            } ?: NOTIFICATION_CHANNEL_ID
        } catch (e: Error) { NOTIFICATION_CHANNEL_ID }

    private fun showNotification(
        notification: RemoteMessage.Notification,
        channelId: String
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder: Notification.Builder = Notification.Builder(this, channelId)
        notificationBuilder.setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setStyle(Notification.BigTextStyle())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notification.title ?: "")
            .setContentText(notification.body ?: "")
        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        viewModel.onNewToken(s)
        Log.d("NEW_TOKEN", s)
    }

    /*private fun showNotification(message: RemoteMessage.Notification?) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder: Notification.Builder =
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message?.title ?: "")
            .setContentText(message?.body ?: "")
        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
    }*/

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}
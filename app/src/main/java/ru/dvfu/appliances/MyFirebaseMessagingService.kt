package ru.dvfu.appliances

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService
import org.koin.android.ext.android.get
import ru.dvfu.appliances.model.FirebaseMessagingViewModel
import ru.dvfu.appliances.model.utils.Constants.NOTIFICATION_CHANNEL_ID
import java.util.*


class MyFirebaseMessagingService() : FirebaseMessagingService() {

    val viewModel: FirebaseMessagingViewModel = get()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.body?.let {
            Log.d("MSG", it)
        }

        remoteMessage.notification?.let {
            showNotification(notification = it, data = remoteMessage.data)
        }
    }

    private fun showNotification(notification: RemoteMessage.Notification, data: Map<String, String>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder: Notification.Builder =
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
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

    private fun showNotification(message: RemoteMessage.Notification?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder: Notification.Builder =
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message?.title ?: "")
            .setContentText(message?.body ?: "")
        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}
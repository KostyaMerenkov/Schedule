package ru.dvfu.appliances

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get
import ru.dvfu.appliances.model.FirebaseMessagingViewModel
import ru.dvfu.appliances.model.utils.Constants
import ru.dvfu.appliances.model.utils.Constants.NOTIFICATION_CHANNEL_ID
import java.util.*


class MyFirebaseMessagingService() : FirebaseMessagingService() {

    val viewModel: FirebaseMessagingViewModel = get()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.body?.let {
            Log.d("MSG", it)
        }

        remoteMessage.notification?.let {
            val channelId = getNotificationTypeChannel(data = remoteMessage.data)
            showNotification(this, notification = it, channelId = channelId)
        }
    }

    private fun getNotificationTypeChannel(data: Map<String, String>): String =
        data["notificationType"]?.let { Constants.NotificationType.valueOf(it).channelId } ?: NOTIFICATION_CHANNEL_ID

    private fun showNotification(
        context: Context,
        notification: RemoteMessage.Notification,
        channelId: String
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        runBlocking { delay(12000L) }
        var bitmap: Bitmap? = null
        notification.imageUrl?.let {
            val futureTarget = Glide.with(context)
                .asBitmap()
                .load(it)
                .submit()

            bitmap = futureTarget.get()
            Glide.with(context).clear(futureTarget)
        }

        val notificationBuilder: Notification.Builder = Notification.Builder(this, channelId)
        notificationBuilder.setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setStyle(Notification.BigTextStyle())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notification.title ?: "")
            .setContentText(notification.body ?: "")
            .apply { if (notification.imageUrl != null) setLargeIcon(bitmap) }
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
package ru.dvfu.appliances

import android.app.Notification
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("MSG", remoteMessage.notification!!.body!!)
        shownotification(remoteMessage.notification)
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("NEW_TOKEN", s)
    }

    fun shownotification(message: RemoteMessage.Notification?) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "com.dvfu.appliances" //your app package name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "Techrush Channel"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder: Notification.Builder =
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle(message!!.title)
            .setContentText(message.body)
            .setContentInfo("Info")
        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
    }
}
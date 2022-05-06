package ru.dvfu.appliances.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.dvfu.appliances.BuildConfig
import ru.dvfu.appliances.di.*
import ru.dvfu.appliances.model.utils.Constants

class Schedule : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Koin Android logger
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            //inject Android context
            androidContext(this@Schedule)
            modules(
                listOf(
                    application,
                    mainActivity,
                    repositoryModule
                )
            )
        }

        if (BuildConfig.DEBUG) { FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false) }
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        createMainNotificationChannel()
        createApplianceNotificationChannel()
        createEventNotificationChannel()
        createMyEventNotificationChannel()
        createNewEventNotificationChannel()
    }

    private fun createMyEventNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            Constants.MY_EVENT_CHANNEL_ID, "Мои события",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = "Изменения моих событий"
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createNewEventNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            Constants.NEW_EVENT_CHANNEL_ID, "Новые события",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = ""
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createEventNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            Constants.EVENT_CHANNEL_ID, "События",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = "Изменения событий"
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createApplianceNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            Constants.APPLIANCE_CHANNEL_ID, "Приборы",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = "Изменения приборов"
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createMainNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID, "Основные",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = "Основные оповещения приложения"
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
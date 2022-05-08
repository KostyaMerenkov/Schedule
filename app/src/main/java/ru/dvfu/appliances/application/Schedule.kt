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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Constants.NotificationType.values().forEach { notificationType ->

            val notificationChannel = NotificationChannel(
                notificationType.channelId, notificationType.title,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = notificationType.description
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}
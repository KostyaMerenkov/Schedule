package ru.dvfu.appliances.application

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.dvfu.appliances.BuildConfig
import ru.dvfu.appliances.di.*

class Schedule : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Koin Android logger
            if (BuildConfig.DEBUG)
                androidLogger()
            //inject Android context
            androidContext(this@Schedule)
            modules(
                listOf(
                    application,
                    mainActivity
                )
            )
        }
    }
}
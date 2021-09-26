package ru.dvfu.appliances.application

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.dvfu.appliances.di.appliancesScreen
import ru.dvfu.appliances.di.application
import ru.dvfu.appliances.di.loginScreen
import ru.dvfu.appliances.di.usersScreen

class ScheduleApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Koin Android logger
            androidLogger(org.koin.core.logger.Level.NONE)
            //inject Android context
            androidContext(this@ScheduleApp)
            //androidContext(applicationContext)
            modules(listOf(application, loginScreen, usersScreen, appliancesScreen))
        }
    }
}
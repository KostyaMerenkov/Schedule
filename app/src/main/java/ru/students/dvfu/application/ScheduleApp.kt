package ru.students.dvfu.application

import android.app.Application
import android.app.usage.UsageEvents.Event.NONE
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.students.dvfu.di.appliancesScreen
import ru.students.dvfu.di.application
import ru.students.dvfu.di.usersScreen
import java.util.logging.Level

class ScheduleApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Koin Android logger
            androidLogger(org.koin.core.logger.Level.NONE)
            //inject Android context
            androidContext(this@ScheduleApp)
            //androidContext(applicationContext)
            modules(listOf(application, usersScreen, appliancesScreen))
        }
    }
}
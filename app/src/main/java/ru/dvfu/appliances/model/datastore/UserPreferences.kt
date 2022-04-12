package ru.dvfu.appliances.model.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import ru.dvfu.appliances.compose.calendars.CalendarType
import ru.dvfu.appliances.model.repository.entity.User


class UserDatastoreImpl(private val context: Context): UserDatastore {

    // to make sure there's only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userSettings")
        private val USER = stringPreferencesKey("USER")
        private val CALENDAR_TYPE = stringPreferencesKey("CALENDAR")

    }

    override val getCurrentUser: Flow<User> = context.dataStore.data
        .map { preferences ->
            Gson().fromJson(preferences[USER], User::class.java) ?: User()
        }

    override val getCalendarType: Flow<CalendarType> = context.dataStore.data
        .map { preferences ->
            CalendarType.valueOf(preferences[CALENDAR_TYPE] ?: CalendarType.WEEK.name)
        }.catch { e ->
            if (e is IllegalArgumentException) { emit(CalendarType.WEEK) }
        }

    override suspend fun saveCalendarType(calendarType: CalendarType) {
        context.dataStore.edit { preferences ->
            preferences[CALENDAR_TYPE] = calendarType.name
        }
    }

    override suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER] = Gson().toJson(user)
        }
    }


}

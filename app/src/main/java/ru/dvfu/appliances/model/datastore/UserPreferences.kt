package ru.dvfu.appliances.model.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import ru.dvfu.appliances.model.repository.entity.User


class UserPreferencesImpl(private val context: Context): UserDatastore {

    // to make sure there's only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userSettings")
        private val USER = stringPreferencesKey("USER")

    }

    override val getCurrentUser: Flow<User> = context.dataStore.data
        .map { preferences ->
            Gson().fromJson(preferences[USER], User::class.java) ?: User()
        }

    override suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER] = Gson().toJson(user)
        }
    }


}

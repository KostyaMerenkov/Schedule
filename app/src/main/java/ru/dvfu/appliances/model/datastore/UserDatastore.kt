package ru.dvfu.appliances.model.datastore

import kotlinx.coroutines.flow.Flow
import ru.dvfu.appliances.model.repository.entity.User

interface UserDatastore {
    val getCurrentUser: Flow<User>
    suspend fun saveUser(user: User)
}
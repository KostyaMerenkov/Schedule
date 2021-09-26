package ru.dvfu.appliances.model.auth

import kotlinx.coroutines.flow.Flow
import ru.dvfu.appliances.model.repository.entity.User

interface AuthManager {
    val currentUser: Flow<User?>
    suspend fun logoutCurrentUser(): Flow<Boolean>
}
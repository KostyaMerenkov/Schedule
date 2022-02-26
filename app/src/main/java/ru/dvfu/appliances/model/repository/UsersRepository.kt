package ru.dvfu.appliances.model.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.Progress

interface UsersRepository {
    val currentUser: Flow<User?>
    val currentUserFromDB: Flow<User>

    suspend fun getUsers(): Flow<List<User>>

    suspend fun logoutCurrentUser(): Flow<Boolean>
    suspend fun addNewUser(user: User): StateFlow<Progress>
    suspend fun getUserWithId(userId: String): Flow<User>
    suspend fun updateUserField(userId: String, data: Map<String, Any>)
}
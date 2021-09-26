package ru.dvfu.appliances.model.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.dvfu.appliances.model.auth.AuthManager
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.Progress

class UserRepositoryImpl(private val authManager: AuthManager,
                         private val databaseProvider: DatabaseProvider): UserRepository {

    override val currentUser: Flow<User?>
        get() = authManager.currentUser

    override suspend fun logoutCurrentUser() = authManager.logoutCurrentUser()
    override suspend fun addNewUser(user: User): StateFlow<Progress> = databaseProvider.addNewUser(user)
}
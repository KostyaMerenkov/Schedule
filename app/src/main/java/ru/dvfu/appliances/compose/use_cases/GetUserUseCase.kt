package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository

class GetUserUseCase(
    private val offlineRepository: OfflineRepository,
    private val usersRepository: UsersRepository
) {

    suspend operator fun invoke(
        userId: String
    ) = flow<Result<User>> {
        offlineRepository.getUser(userId).catch { getUserOnline(this, userId) }.collect {
            it.fold(
                onSuccess = {
                    emit(Result.success(it))
                },
                onFailure = {
                    getUserOnline(this, userId)
                }
            )
        }
    }

    private suspend fun getUserOnline(
        flowCollector: FlowCollector<Result<User>>,
        userId: String
    ) {
        usersRepository.getUser(userId).fold(
            onSuccess = {
                flowCollector.emit(Result.success(it))
            },
            onFailure = {
                flowCollector.emit(Result.failure(it))
            }
        )
    }
}
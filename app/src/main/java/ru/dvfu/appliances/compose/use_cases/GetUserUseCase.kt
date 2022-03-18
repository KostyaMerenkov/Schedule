package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.channelFlow
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository

class GetUserUseCase(
    private val offlineRepository: OfflineRepository,
    private val usersRepository: UsersRepository
) {

    suspend operator fun invoke(
        userId: String
    ) = channelFlow<Result<User>> {
        offlineRepository.getUser(userId).collect {
            it.fold(
                onSuccess = {
                    send(Result.success(it))
                },
                onFailure = {
                    getUserOnline(this, userId)
                }
            )
        }
    }

    private suspend fun getUserOnline(
        flowCollector: ProducerScope<Result<User>>,
        userId: String
    ) {
        usersRepository.getUser(userId).collect {
            it.fold(
                onSuccess = {
                    flowCollector.send(Result.success(it))
                },
                onFailure = {
                    flowCollector.send(Result.failure(it))
                }
            )
        }
    }
}
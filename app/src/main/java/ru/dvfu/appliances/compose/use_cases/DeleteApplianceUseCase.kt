package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository

class DeleteApplianceUseCase(
    private val appliancesRepository: AppliancesRepository,
    private val eventsRepository: EventsRepository,
) {

    suspend operator fun invoke(
        applianceId: String
    ) = flow<Result<Unit>> {
        val eventsResult = eventsRepository.deleteAllApplianceEvents(applianceId)
        if (eventsResult.isFailure) emit(eventsResult)
        else {
            val applianceDeletionResult = appliancesRepository.deleteAppliance(applianceId)
            emit(applianceDeletionResult)
        }
    }

}
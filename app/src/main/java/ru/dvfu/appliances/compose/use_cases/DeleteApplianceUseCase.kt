package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.EventsRepository

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
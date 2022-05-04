package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.OfflineRepository

class GetApplianceUseCase(
    private val offlineRepository: OfflineRepository,
    private val appliancesRepository: AppliancesRepository
) {

    suspend operator fun invoke(
        applianceId: String
    ) = flow<Result<Appliance>> {
        offlineRepository.getApplianceById(applianceId).catch { getApplianceOnline(this, applianceId) }.collect {
            it.fold(
                onSuccess = {
                    emit(Result.success(it))
                },
                onFailure = {
                    getApplianceOnline(this, applianceId)
                }
            )
        }
    }

    private suspend fun getApplianceOnline(
        flowCollector: FlowCollector<Result<Appliance>>,
        applianceId: String
    ) {
        appliancesRepository.getAppliance(applianceId).collect {
            it.fold(
                onSuccess = {
                    flowCollector.emit(Result.success(it))
                },
                onFailure = {
                    flowCollector.emit(Result.failure(it))
                }
            )
        }
    }
}
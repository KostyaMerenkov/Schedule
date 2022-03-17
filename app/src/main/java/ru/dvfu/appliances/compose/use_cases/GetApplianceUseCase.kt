package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.single
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository_offline.OfflineRepository

class GetApplianceUseCase(
    private val offlineRepository: OfflineRepository,
    private val appliancesRepository: AppliancesRepository
    ) {

        suspend operator fun invoke(
            applianceId: String
        ) = channelFlow<Result<Appliance>> {
            offlineRepository.getApplianceById(applianceId).collect {
                it.fold(
                onSuccess = {
                    send(Result.success(it))
                },
                onFailure = {
                    getApplianceOnline(this, applianceId)
                }
            )
            }
        }

    private suspend fun getApplianceOnline(flowCollector: ProducerScope<Result<Appliance>>, applianceId: String) {
        appliancesRepository.getAppliance(applianceId).single().fold(
            onSuccess = {
                flowCollector.send(Result.success(it))
            },
            onFailure = {
                flowCollector.send(Result.failure(it))
            }
        )
    }
}
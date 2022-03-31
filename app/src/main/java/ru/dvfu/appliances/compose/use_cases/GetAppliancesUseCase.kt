package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository_offline.OfflineRepository

class GetAppliancesUseCase(
    private val offlineRepository: OfflineRepository,
    private val appliancesRepository: AppliancesRepository
    ) {

        suspend operator fun invoke(
            applianceId: String
        ) = flow<Result<List<Appliance>>> {
            /*offlineRepository.getAppliances(applianceId).collect {
                it.fold(
                onSuccess = {
                    send(Result.success(it))
                },
                onFailure = {
                    getAppliancesOnline(this, applianceId)
                }
            )
            }*/
        }

    private suspend fun getAppliancesOnline(flowCollector: ProducerScope<Result<Appliance>>, applianceId: String) {
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
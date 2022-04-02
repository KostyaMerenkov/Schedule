package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository_offline.OfflineRepository

class GetAppliancesUseCase(
    private val offlineRepository: OfflineRepository,
    private val appliancesRepository: AppliancesRepository
) {

    suspend operator fun invoke() = flow<List<Appliance>> {
        offlineRepository.getAppliances().collect {
            if (it.isEmpty()) getAppliancesOnline(this)
            else emit(it)
        }
    }

    private suspend fun getAppliancesOnline(flowCollector: FlowCollector<List<Appliance>>) {
        flowCollector.emit(appliancesRepository.getAppliancesOneTime().getOrDefault(listOf()))
    }
}
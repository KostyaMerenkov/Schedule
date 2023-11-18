package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.*
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.OfflineRepository

class GetAppliancesUseCase(
    private val offlineRepository: OfflineRepository,
    private val appliancesRepository: AppliancesRepository
) {

    suspend operator fun invoke() = flow<Result<List<Appliance>>> {
        offlineRepository.getAppliances().catch { getAppliancesOnline(this) }.collect {
            it.fold(
                onSuccess = {
                    emit(Result.success(it))
                },
                onFailure = {
                    getAppliancesOnline(this)
                }
            )
        }
    }

    private suspend fun getAppliancesOnline(flowCollector: FlowCollector<Result<List<Appliance>>>) {
        flowCollector.emit(appliancesRepository.getAppliancesOneTime())
    }
}
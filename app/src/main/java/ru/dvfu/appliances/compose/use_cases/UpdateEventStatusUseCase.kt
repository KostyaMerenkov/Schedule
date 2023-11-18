package ru.dvfu.appliances.compose.use_cases

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.dvfu.appliances.compose.utils.NotificationManager
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent

class UpdateEventStatusUseCase(
    private val eventsRepository: EventsRepository,
    private val userDatastore: UserDatastore,
    private val notificationManager: NotificationManager,
) {
    suspend operator fun invoke(
        event: CalendarEvent,
        newStatus: BookingStatus,
        managerCommentary: String,
    ) = flow<Result<Unit>>{
        val currentUser = userDatastore.getCurrentUser.first()

        val result = eventsRepository.setNewEventStatus(event.id, newStatus, managerCommentary, currentUser.userId)
        if (result.isSuccess) {
            notificationManager.newEventStatus(event, newStatus)
        }
        emit(result)
    }
}
package ru.dvfu.appliances.compose.use_cases

import ru.dvfu.appliances.compose.use_cases.event.UpdateEventUserCommentUseCase
import ru.dvfu.appliances.compose.use_cases.event.UpdateManagerCommentUseCase
import ru.dvfu.appliances.compose.use_cases.event.UpdateTimeUseCase
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.utils.toMillis

class UpdateEventUseCase(
    val updateUserCommentUseCase: UpdateEventUserCommentUseCase,
    val updateManagerCommentUseCase: UpdateManagerCommentUseCase,
    val updateEventStatusUseCase: UpdateEventStatusUseCase,
    val updateTimeUseCase: UpdateTimeUseCase,
)
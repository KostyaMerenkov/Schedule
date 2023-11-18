package ru.dvfu.appliances.model.repository.entity.notifications

import ru.dvfu.appliances.model.utils.Constants

data class NotificationData(
    val notificationType: Constants.NotificationType = Constants.NotificationType.DEFAULT
)

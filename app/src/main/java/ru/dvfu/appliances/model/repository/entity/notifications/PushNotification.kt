package ru.dvfu.appliances.model.repository.entity.notifications

data class PushNotification(
    //val data: NotificationData,
    val to: String,
    val notification: NotificationData,
)

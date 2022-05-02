package ru.dvfu.appliances.model.repository.entity.notifications

data class PushNotification(
    val to: String,
    val notification: Notification,
    //val data: NotificationData = NotificationData(),
    )

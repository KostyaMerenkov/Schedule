package ru.dvfu.appliances.model.utils

import androidx.compose.ui.graphics.Color

object Constants {
    const val SENDER_ID = 106372275715

    const val TIME_TO_EXIT = 2000L

    enum class NotificationType(val channelId: String = NOTIFICATION_CHANNEL_ID, val title: String, val description: String) {
        APPLIANCE("channel_appliance", "Приборы", "Изменения приборов"),
        EVENT("channel_event", "События", "Изменения событий"),
        NEW_EVENT("channel_new_event", "Новые события", "Для суперпользователей и администраторов"),
        MY_EVENT("channel_my_event", "Мои события", "Изменения моих событий"),
        DEFAULT(title = "Основные", description = "Основные оповещения приложения");
    }

    const val NOTIFICATION_CHANNEL_ID = "com.dvfu.appliances" //your app package name

    val DEFAULT_EVENT_COLOR = Color.Gray

    const val TAG = "SCHEDULE"
}
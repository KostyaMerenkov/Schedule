package ru.dvfu.appliances.compose.event_calendar

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

data class Event(
    val id: String = "",
    val name: String = "",
    val color: Color = Color.White,
    val userId: String = "",
    val start: LocalDateTime = LocalDateTime.now(),
    val end: LocalDateTime = LocalDateTime.now(),
    val description: String = "",
)

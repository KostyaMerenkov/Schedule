package ru.dvfu.appliances.model.utils

import java.text.SimpleDateFormat
import java.time.*
import java.util.*

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun Long.toZonedDateTime(): ZonedDateTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault())
}

fun Long.toLocalTime(): LocalTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault()).toLocalTime()
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault()).toLocalDate()
}

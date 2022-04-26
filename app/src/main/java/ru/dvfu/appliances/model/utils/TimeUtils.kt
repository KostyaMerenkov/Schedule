package ru.dvfu.appliances.model.utils

import ru.dvfu.appliances.model.utils.TimeConstants.ZONE
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


object TimeConstants {
    val FULL_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    val MIN_EVENT_DURATION: Duration = Duration.ofMinutes(30)

    val ZONE: ZoneId = ZoneId.of("Asia/Vladivostok")

}

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZONE).toLocalDateTime()
}

fun Long.toZonedDateTime(): ZonedDateTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZONE)
}

fun Long.toLocalTime(): LocalTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZONE).toLocalTime()
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZONE).toLocalDate()
}


private val ZonedDateTime.toMillis: Long
    get() = this.toInstant().toEpochMilli()

val LocalDateTime.toMillis: Long
    get() = this.atZone(ZONE).toInstant().toEpochMilli()

val LocalDate.toMillis: Long
    get() = this.atStartOfDay().atZone(ZONE).toMillis

fun LocalTime.toHoursAndMinutes(): String {
    return format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
}
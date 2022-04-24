package ru.dvfu.appliances.compose.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object TimeConstants {
    const val MILLISECONDS_IN_MINUTE = 60000L

    val FULL_DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
}

val LocalDateTime.toMillis: Long
    get() = this.atZone(ZoneId.of("Asia/Vladivostok")).toInstant().toEpochMilli()

val LocalDate.toMillis: Long
    get() = this.atStartOfDay().toMillis

fun LocalTime.toHoursAndMinutes(): String {
    return format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
}
package ru.dvfu.appliances.compose.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

object TimeConstants {
    const val MILLISECONDS_IN_DAY = 86400000L
    const val SECONDS_IN_DAY = 86400L
    const val MILLISECONDS_IN_SECOND = 1000L
    const val MILLISECONDS_IN_HOUR = 3600000L
    const val SECONDS_IN_HOUR = 3600L
    const val SECONDS_IN_MINUTE = 60L
    const val MILLISECONDS_IN_MINUTE = 60000L
    const val MOON_PHASE_INCREMENT_IN_DAY = 0.03f


    val FULL_DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

}

val LocalDateTime.toMillis: Long
    get() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

val LocalDate.toMillis: Long
    get() = this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
package ru.dvfu.appliances.model.utils

import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.utils.TimeConstants.ZONE
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


object TimeConstants {
    val FULL_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    val MIN_EVENT_DURATION: Duration = Duration.ofMinutes(30)
    val DEFAULT_EVENT_DURATION: Duration = Duration.ofHours(1)
    const val MINUTES_BEFORE_END = 5


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

fun formattedTime(timeStart: LocalDateTime, timeEnd: LocalDateTime): String {
    return "${
        timeStart.toLocalTime().toHoursAndMinutes()
    } - ${
        timeEnd.toLocalTime().toHoursAndMinutes()
    }"
}

fun formattedDate(date: LocalDate): String {
    return date.format(DateTimeFormatter.ofPattern("d MMMM"))
}

fun formattedDateTime(date: LocalDate, timeStart: LocalDateTime, timeEnd: LocalDateTime) =
    "${formattedDate(date)}, ${formattedTime(timeStart, timeEnd)}"

fun formattedDateTimeStatus(
    date: LocalDate,
    timeStart: LocalDateTime,
    timeEnd: LocalDateTime,
    status: BookingStatus
) = "${formattedDate(date)}, ${formattedTime(timeStart, timeEnd)}, ${status.getName().uppercase()}"

fun formattedAppliance(name: String) = "\"${name}\""

fun formattedApplianceDateTime(
    name: String,
    date: LocalDate,
    timeStart: LocalDateTime,
    timeEnd: LocalDateTime,
) = "${formattedAppliance(name)}, ${formattedDate(date)}, ${formattedTime(timeStart, timeEnd)}"

fun formattedApplianceDateTimeStatus(
    name: String,
    date: LocalDate,
    timeStart: LocalDateTime,
    timeEnd: LocalDateTime,
    status: BookingStatus,
) = "${formattedAppliance(name)}, ${formattedDateTimeStatus(date, timeStart, timeEnd, status)}"

val LocalDateTime.toDateAndTime: String
    get() = this.format(DateTimeFormatter.ofPattern("d MMMM, HH:mm"))
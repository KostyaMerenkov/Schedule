package ru.dvfu.appliances.compose.components

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDate(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(date)
}

fun Long.toDateWithWeek(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
    return sdf.format(date)
}

fun Long.toTime(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(date)
}
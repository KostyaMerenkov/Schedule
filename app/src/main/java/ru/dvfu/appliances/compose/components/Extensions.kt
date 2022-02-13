package ru.dvfu.appliances.compose.components

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDate(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    return sdf.format(date)
}

fun Long.toTime(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(date)
}
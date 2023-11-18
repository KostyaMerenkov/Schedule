package ru.dvfu.appliances.compose.components

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import java.time.*
import java.util.*
import kotlin.math.min

@Composable
fun DatePicker(
    context: Context,
    date: LocalDate,
    minDate: LocalDate = LocalDate.now(),
    onDateSet: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        android.R.style.Theme_Material_Dialog_Alert,
        { _, year, monthOfYear, dayOfMonth ->
            onDateSet(LocalDate.of(year, monthOfYear.inc(), dayOfMonth))
        },
        date.year,
        date.monthValue.dec(),
        date.dayOfMonth
    ).apply {
        datePicker.minDate = minDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        show()
    }
    onDismiss()
}

@Composable
fun TimePicker(
    context: Context,
    time: LocalTime,
    onTimeSet: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {

    TimePickerDialog(
        context,
        R.style.Theme_Material_Dialog_Alert,
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            onTimeSet(LocalTime.of(hourOfDay, minute))
        },
        time.hour,
        time.minute, true
    ).show()
    onDismiss.invoke()
}
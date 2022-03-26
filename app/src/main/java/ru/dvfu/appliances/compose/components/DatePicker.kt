package ru.dvfu.appliances.compose.components

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import java.util.*

@Composable
fun DatePicker(
    date: MutableState<Long>,
    dateSetState: MutableState<Boolean>,
    context: Context
) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        android.R.style.Theme_Material_Dialog_Alert,
        { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            date.value = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply { show() }
    dateSetState.value = false
}

@Composable
fun TimePicker(
    context: Context,
    date: Long,
    onTimeSet: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = date
    }

    TimePickerDialog(
        context,
        R.style.Theme_Material_Dialog_Alert,
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            onTimeSet(calendar.timeInMillis)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE), true
    ).show()
    onDismiss.invoke()
}
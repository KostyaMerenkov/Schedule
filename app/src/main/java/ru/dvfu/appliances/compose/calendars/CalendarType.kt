package ru.dvfu.appliances.compose.calendars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.ui.graphics.vector.ImageVector
import ru.dvfu.appliances.R
import ru.dvfu.appliances.model.utils.StringOperation

enum class CalendarType(override val stringRes: Int, val icon: ImageVector): StringOperation {
    WEEK(R.string.week, Icons.Default.CalendarViewWeek),
    MONTH(R.string.month, Icons.Default.CalendarViewMonth),
    THREE_DAYS(R.string.three_days, Icons.Default.CalendarViewDay);
}
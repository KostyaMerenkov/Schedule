package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.first
import kotlinx.parcelize.Parcelize
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.model.utils.StringOperation
import ru.dvfu.appliances.model.utils.toLocalDate
import ru.dvfu.appliances.model.utils.toLocalDateTime
import ru.dvfu.appliances.model.utils.toLocalTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Parcelize
data class Event(
    val id: String = UUID.randomUUID().toString(),
    val date: Long = 0L,
    val timeCreated: Long = 0L,
    val timeStart: Long = 0L,
    val timeEnd: Long = 0L,
    val commentary: String = "0",
    val applianceId: String = "0",
    val userId: String = "0",
    val managedById: String? = null,
    val managedTime: Long? = null,
    val managerCommentary: String = "",
    val status: BookingStatus = BookingStatus.NONE,
) : Parcelable


@Parcelize
data class CalendarEvent(
    val id: String = "",
    val date: LocalDate,
    val timeCreated: LocalDateTime,
    var timeStart: LocalDateTime,
    var timeEnd: LocalDateTime,
    var commentary: String = "",
    val user: User = User(),
    var appliance: Appliance = Appliance(),
    val managedUser: User? = null,
    val managedTime: LocalDateTime? = null,
    val managerCommentary: String = "",
    val status: BookingStatus = BookingStatus.NONE,
) : Parcelable

@Parcelize
enum class BookingStatus(override val stringRes: Int) : StringOperation, Parcelable {
    NONE(R.string.new_books),
    APPROVED(R.string.approved_books),
    DECLINED(R.string.declined_books),
}

suspend fun Event.toCalendarEvent(
    getUserUseCase: GetUserUseCase,
    getApplianceUseCase: GetApplianceUseCase
): CalendarEvent = run {
    CalendarEvent(
        id = id,
        date = date.toLocalDate(),
        timeCreated = timeCreated.toLocalDateTime(),
        timeStart = timeStart.toLocalDateTime(),
        timeEnd = timeEnd.toLocalDateTime(),
        commentary = commentary,
        user = getUserUseCase(userId).first().getOrDefault(User()),
        appliance = getApplianceUseCase(applianceId).first().getOrDefault(Appliance()),
        managedUser = managedById?.let {
            getUserUseCase(managedById).first().getOrDefault(
                User()
            )
        },
        managedTime = managedTime?.toLocalDateTime(),
        managerCommentary = managerCommentary,
        status = status,
    )
}

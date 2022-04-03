package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.dvfu.appliances.R
import ru.dvfu.appliances.model.utils.StringOperation
import java.time.LocalDateTime

@Parcelize
data class Booking(
    val id: String = "",
    var timeCreated: Long = 0L,
    var timeStart: Long = 0L,
    var timeEnd: Long = 0L,
    var commentary: String = "",
    val userId: String = "",
    var applianceId: String = "",
    val managedById: String = "",
    val managedTime: Long = 0L,
    val managerCommentary: String = "",
    val status: BookingStatus = BookingStatus.NONE
    ) : Parcelable

@Parcelize
data class UiBooking(
    val id: String,
    var timeStart: LocalDateTime,
    var timeEnd: LocalDateTime,
    var commentary: String,
    val user: User,
    var appliance: Appliance,
    val managedUser: User?,
    val managedTime: Long,
    val managerCommentary: String,
    val status: BookingStatus,
) : Parcelable

enum class BookingStatus(override val stringRes: Int): StringOperation {
    NONE(R.string.new_books),
    APPROVED(R.string.approved_books),
    DECLINED(R.string.declined_books),
}

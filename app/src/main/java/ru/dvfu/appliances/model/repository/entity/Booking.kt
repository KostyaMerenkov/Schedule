package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(
    val id: String = "",
    val timeStart: Long = 0L,
    val timeEnd: Long = 0L,
    val commentary: String = "",
    val applianceId: String = "",
    val applianceName: String = "",
    val superUserId: String = "",
    val managedById: String = "",
    val managerCommentary: String = "",
    val status: BookingStatus = BookingStatus.NONE
    ) : Parcelable

enum class BookingStatus {
    APPROVED,
    DECLINED,
    NONE;
}

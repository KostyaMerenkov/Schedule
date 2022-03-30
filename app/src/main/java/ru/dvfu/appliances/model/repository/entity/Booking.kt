package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(
    val id: String = "",
    var timeStart: Long = 0L,
    var timeEnd: Long = 0L,
    var commentary: String = "",
    val userId: String = "",
    var applianceId: String = "",
    //val applianceName: String = "",
    val managedById: String = "",
    val managerCommentary: String = "",
    val status: BookingStatus = BookingStatus.NONE
    ) : Parcelable

enum class BookingStatus {
    APPROVED,
    DECLINED,
    NONE;
}

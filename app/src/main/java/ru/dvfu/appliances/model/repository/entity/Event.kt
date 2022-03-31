package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Event(
    val id: String = "",
    val timeStart: Long = 0L,
    val timeEnd: Long = 0L,
    val commentary: String = "",
    val applianceId: String = "",
    val applianceName: String = "",
    val color: Int = 0,
    val userId: String = "",
    val superUserId: String? = null,
    val approved: Boolean = false,
    val approvedBy: String = "",

    ) : Parcelable

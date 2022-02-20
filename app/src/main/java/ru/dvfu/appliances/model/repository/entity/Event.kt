package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val id: String = "",
    val timeStart: Long = 0L,
    val timeEnd: Long = 0L,
    val applianceId: String = "",
    val superUserId: String? = null,
    val approved: Boolean = false,

) : Parcelable

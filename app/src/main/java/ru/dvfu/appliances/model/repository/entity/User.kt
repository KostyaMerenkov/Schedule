package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val userId: String = "0",
    val userName: String = "Anonymous",
    val email: String = "",
    val role: Int = Role.GUEST.ordinal,
    val isAnonymous: Boolean = true,
    val userPic: String? = null,

    ): Parcelable
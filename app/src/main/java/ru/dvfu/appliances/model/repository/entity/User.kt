package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.dvfu.appliances.model.userdata.entities.Role

@Parcelize
data class User(
    val userId: String = "0",
    val userName: String = "Anonymous",
    val email: String = "",
    val role: Int = Role.GUEST.ordinal,
    val isAnonymous: Boolean = true,
    val userPic: String? = null,
    //val appliances: List<Appliance> = listOf()
): Parcelable
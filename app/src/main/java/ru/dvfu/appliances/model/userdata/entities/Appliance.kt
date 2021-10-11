package ru.dvfu.appliances.model.userdata.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.dvfu.appliances.model.repository.entity.User

@Parcelize
data class Appliance(
    val name: String = "",
    val owner: String = "",
    val superusers: List<User> = listOf(),
    val users: List<User> = listOf()
): Parcelable

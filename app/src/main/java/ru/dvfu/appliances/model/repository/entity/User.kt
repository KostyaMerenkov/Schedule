package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String = "0",
    val msgToken: String = "",
    val userName: String = "",
    val email: String = "",
    val birthday: Long = 0,
    val role: Int = Roles.GUEST.ordinal,
    val anonymous: Boolean = true,
    val userPic: String = "",
    ): Parcelable {

    fun isAdmin(): Boolean {
        return role == Roles.ADMIN.ordinal
    }
    fun isUser(): Boolean {
        return role == Roles.USER.ordinal
    }
    fun isAnonymousOrGuest(): Boolean {
        return role == Roles.GUEST.ordinal || anonymous
    }
}
package ru.dvfu.appliances.model.repository.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.dvfu.appliances.BuildConfig
import java.time.LocalDateTime

@Parcelize
data class User(
    val userId: String = "0",
    val msgToken: String = "",
    val userName: String = "",
    val email: String = "",
    val birthday: Long = 0,
    val role: Int = Roles.GUEST.ordinal,
    val anonymous: Boolean = false,
    val userPic: String = "",
) : Parcelable

val User.isAdmin: Boolean
    get() = role == Roles.ADMIN.ordinal

val User.isAnonymousOrGuest: Boolean
    get() = role == Roles.GUEST.ordinal || anonymous

fun User.canManageEvent(event: CalendarEvent): Boolean {
    return ((isAdmin || event.appliance.superuserIds.contains(userId) )
            && event.timeEnd.isAfter(LocalDateTime.now())/* || BuildConfig.DEBUG*/)
}


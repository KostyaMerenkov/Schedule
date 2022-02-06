package ru.dvfu.appliances.model.repository.entity

import androidx.compose.ui.semantics.Role
import ru.dvfu.appliances.R

enum class Roles(val stringRes: Int, val stringRess: Int) {
    GUEST(R.string.guest, R.string.guests),
    USER(R.string.user, R.string.users),
    SUPERUSER(R.string.superuser, R.string.superusers),
    ADMIN(R.string.admin, R.string.admins);

    fun isAdmin(): Boolean {
        return when(this) {
            ADMIN -> true
            else -> false
        }
    }



}

fun getRole(ordinal: Int): Roles {
    return when (ordinal) {
        Roles.GUEST.ordinal -> Roles.GUEST
        Roles.USER.ordinal -> Roles.USER
        Roles.SUPERUSER.ordinal -> Roles.SUPERUSER
        Roles.ADMIN.ordinal -> Roles.ADMIN
        else -> Roles.GUEST
    }
}
package ru.dvfu.appliances.model.repository.entity

import androidx.compose.ui.semantics.Role
import ru.dvfu.appliances.R

enum class Roles(val stringRes: Int) {
    GUEST(R.string.guest),
    USER(R.string.user),
    SUPERUSER(R.string.superuser),
    ADMIN(R.string.admin);

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
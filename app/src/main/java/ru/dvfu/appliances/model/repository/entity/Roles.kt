package ru.dvfu.appliances.model.repository.entity

import androidx.compose.ui.semantics.Role
import ru.dvfu.appliances.R
import ru.dvfu.appliances.model.utils.StringOperation

enum class Roles(override val stringRes: Int, val stringRess: Int): StringOperation {
    GUEST(R.string.guest, R.string.guests),
    USER(R.string.user, R.string.users),
    //SUPERUSER(R.string.superuser, R.string.superusers),
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
        //Roles.SUPERUSER.ordinal -> Roles.SUPERUSER
        Roles.ADMIN.ordinal -> Roles.ADMIN
        else -> Roles.GUEST
    }
}
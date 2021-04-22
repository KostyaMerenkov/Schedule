package ru.students.dvfu.mvp.model.entity

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class FirebaseUser(
    val name: String = "",
    val email: String = "",
    val avatar: String = "",
    val role: String = ""
    ) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FirebaseUser

        if (name != other.name) return false
        if (email != other.email) return false
        if (avatar != other.avatar) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + avatar.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}

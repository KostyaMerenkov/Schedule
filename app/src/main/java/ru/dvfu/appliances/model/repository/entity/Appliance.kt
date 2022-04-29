package ru.dvfu.appliances.model.repository.entity

import android.graphics.Color
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.MutableStateFlow
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.Constants
import ru.dvfu.appliances.model.utils.randomUUID
import java.util.*

@Parcelize
data class Appliance(
    val id: String = randomUUID(),
    val name: String = "",
    val description: String = "",
    val color: Int = Constants.DEFAULT_EVENT_COLOR.hashCode(),
    val createdById: String = "",
    val superuserIds: List<String> = listOf(),
    val userIds: List<String> = listOf()
): Parcelable {
    fun isUserSuperuserOrAdmin(user: User): Boolean {
        return superuserIds.contains(user.userId) ||  user.isAdmin()
    }
}

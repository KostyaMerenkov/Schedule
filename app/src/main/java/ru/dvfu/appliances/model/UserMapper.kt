package ru.dvfu.appliances.model

import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.Roles

class UserMapper {

    fun mapUser(firebaseUser: com.firebase.ui.auth.data.model.User, photoLinks: List<String>) =
        User(
            //userId = firebaseUser.,
            userName = "Anonymous",
            email = "",
            role = Roles.GUEST.ordinal,
            isAnonymous = true,
            //userPic = firebaseUser.photoUri.toString() ?: "",
        )
}
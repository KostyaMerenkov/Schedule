package ru.students.dvfu.model.userdata

import ru.students.dvfu.model.userdata.entities.Appliance
import ru.students.dvfu.model.userdata.entities.Role

data class User(
    val name: String = "Guest",
    val email: String = "",
    val avatar: String = "",
    val role: Int = Role.GUEST.ordinal,
    val appliances: List<Appliance> = listOf()
)

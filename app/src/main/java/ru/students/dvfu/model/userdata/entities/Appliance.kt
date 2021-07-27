package ru.students.dvfu.model.userdata.entities

import ru.students.dvfu.model.userdata.User
import java.util.*

data class Appliance(
    val name: String = "",
    val owner: String = "",
    val superusers: List<User> = listOf(),
    val users: List<User> = listOf()
)

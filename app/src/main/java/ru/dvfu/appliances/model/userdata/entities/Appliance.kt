package ru.dvfu.appliances.model.userdata.entities

import ru.dvfu.appliances.model.userdata.User

data class Appliance(
    val name: String = "",
    val owner: String = "",
    val superusers: List<User> = listOf(),
    val users: List<User> = listOf()
)

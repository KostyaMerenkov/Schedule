package ru.dvfu.appliances.compose.appliance

import ru.dvfu.appliances.model.repository.entity.User

data class UserItem(
    val user: User =  User(),
    val isSelected: Boolean,
)
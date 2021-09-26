package ru.dvfu.appliances.model.repository

import com.google.firebase.auth.FirebaseUser
import ru.dvfu.appliances.model.userdata.User
import ru.dvfu.appliances.model.userdata.entities.Appliance

interface Repository {
    suspend fun getUsers(): List<User>
    suspend fun getAppliances(): List<Appliance>

    suspend fun addUser(user: FirebaseUser)
    suspend fun addAppliance(appliance: Appliance)
}
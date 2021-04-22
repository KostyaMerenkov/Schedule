package ru.students.dvfu.mvp.model

import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.core.Single

interface IFirebaseUsersRepo {
    fun isUserInDatabase(UID: String): Boolean

//    fun updateUserInDatabase(user: FirebaseUser)
    fun putUserToDatabase(user: FirebaseUser)
    fun getUsers(): Single<ArrayList<ru.students.dvfu.mvp.model.entity.FirebaseUser>>

}
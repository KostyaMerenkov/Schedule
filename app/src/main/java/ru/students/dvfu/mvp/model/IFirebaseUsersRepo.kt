package ru.students.dvfu.mvp.model

import com.google.firebase.auth.FirebaseUser

interface IFirebaseUsersRepo {
    fun putUserToFirebase(user: FirebaseUser)
}
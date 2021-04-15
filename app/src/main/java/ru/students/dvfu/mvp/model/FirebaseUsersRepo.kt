package ru.students.dvfu.mvp.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class FirebaseUsersRepo: IFirebaseUsersRepo {

    private val database by lazy {
        FirebaseDatabase.getInstance("https://schedule-4c151-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    override fun putUserToFirebase(user: FirebaseUser) {
        //database.reference.
    }
}
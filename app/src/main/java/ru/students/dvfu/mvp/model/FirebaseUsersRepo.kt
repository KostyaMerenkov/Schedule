package ru.students.dvfu.mvp.model

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Semaphore


class FirebaseUsersRepo : IFirebaseUsersRepo {

    var TAG = "FirebaseUsersRepo"

    private val realtimeDatabase by lazy {
        //FirebaseDatabase.getInstance().reference
        FirebaseDatabase.getInstance("https://schedule-4c151-default-rtdb.europe-west1.firebasedatabase.app/")
    }
    private val cloudFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun isUserInDatabase(UID: String): Boolean {
        return false
        TODO("Not yet implemented")
//        return if (cloudFirestore.collection("users").document(UID).get().addOnCompleteListener {
//            return it.isSuccessful
//            }
//                .isComplete) {
//            Log.d(TAG, "User $UID is already in database")
//            true
//        } else {
//            Log.d(TAG, "User $UID is not in database")
//            false
//        }
    }

    override fun putUserToDatabase(user: FirebaseUser) {
        //val firebaseUser = ru.students.dvfu.mvp.model.entity.FirebaseUser(user.displayName, user.email, user.photoUrl.toString(), "new_user")
        cloudFirestore.collection("users").document(user.uid).set(
            hashMapOf(
                "name" to user.displayName,
                "email" to user.email,
                "avatar" to user.photoUrl?.toString(),
                "role" to "new_user"
            )
        ).addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot added with ID: $it")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }

//        val firebaseUser = ru.students.dvfu.mvp.model.entity.FirebaseUser(user.displayName!!, user.email!!, user.photoUrl!!.toString(), "Logged user")
//        realtimeDatabase.reference.child("users").child(user.uid).setValue(firebaseUser)
    }

    override fun getUsers() = Single.fromCallable {
        val semaphore = Semaphore(0)
        val users = ArrayList<ru.students.dvfu.mvp.model.entity.FirebaseUser>()
        cloudFirestore.collection("users").get().addOnSuccessListener { result ->
            for (doc: QueryDocumentSnapshot in result) {
                users.add(doc.toObject(ru.students.dvfu.mvp.model.entity.FirebaseUser::class.java))
            }
            semaphore.release();
        }
        semaphore.acquire()
        return@fromCallable users
    }.subscribeOn(Schedulers.io())
//            .addOnCompleteListener { result ->
//            for (doc: QueryDocumentSnapshot in result) {
//                users.add(doc.toObject(ru.students.dvfu.mvp.model.entity.FirebaseUser::class.java))
//            }
//        }
}

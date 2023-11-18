package ru.dvfu.appliances.model.datasource.deprecated

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.dvfu.appliances.model.repository.entity.User
import java.util.concurrent.Semaphore

class FirebaseUsersRepo {

    var TAG = "FirebaseUsersRepo"

    private val cloudFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

     fun putUserToDatabase(user: FirebaseUser) {
        //val firebaseUser = ru.students.dvfu.mvp.model.userdata.FirebaseUser(user.displayName, user.email, user.photoUrl.toString(), "new_user")
        cloudFirestore.collection("users").document(user.uid).set(
            hashMapOf(
                "name" to user.displayName,
                "email" to user.email,
                "avatar" to user.photoUrl?.toString(),
                "role" to 4
            )
        ).addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot added with ID: $it")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
    }

     fun getUsers(): Single<ArrayList<User>> = Single.fromCallable {
        val semaphore = Semaphore(0)
        val users = ArrayList<User>()
        cloudFirestore.collection("users").get().addOnSuccessListener { result ->
            for (doc: QueryDocumentSnapshot in result) {
                users.add(doc.toObject(User::class.java))
            }
            semaphore.release();
        }
        semaphore.acquire()
        return@fromCallable users
    }.subscribeOn(Schedulers.io())
//            .addOnCompleteListener { result ->
//            for (doc: QueryDocumentSnapshot in result) {
//                users.add(doc.toObject(ru.students.dvfu.mvp.model.userdata.FirebaseUser::class.java))
//            }
//        }
}

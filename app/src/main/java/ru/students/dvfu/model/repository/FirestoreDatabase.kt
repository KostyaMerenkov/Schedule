package ru.students.dvfu.model.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import ru.students.dvfu.model.userdata.User
import ru.students.dvfu.model.userdata.entities.Appliance
import java.util.concurrent.Semaphore

class FirestoreDatabase() : Repository {

    private val realtimeDatabase by lazy {
        //FirebaseDatabase.getInstance().reference
        FirebaseDatabase.getInstance("https://schedule-4c151-default-rtdb.europe-west1.firebasedatabase.app/")
    }
    private val cloudFirestore by lazy { FirebaseFirestore.getInstance() }
    private var TAG = "FirestoreDatabase"

//    override suspend fun getUsers(): Single<ArrayList<User>> = Single.fromCallable {
//        val semaphore = Semaphore(0)
//        val users = ArrayList<User>()
//        cloudFirestore.collection("users").get().addOnSuccessListener { result ->
//            for (doc: QueryDocumentSnapshot in result) {
//                users.add(doc.toObject(User::class.java))
//            }
//            semaphore.release();
//        }
//        semaphore.acquire()
//        return@fromCallable users
//    }.subscribeOn(Schedulers.io())

    override suspend fun getUsers(): List<User> {
        val semaphore = Semaphore(0)
        val users = ArrayList<User>()
        cloudFirestore.collection("users").get().addOnSuccessListener { result ->
            for (doc: QueryDocumentSnapshot in result) {
                users.add(doc.toObject(User::class.java))
            }
            semaphore.release();
        }
        semaphore.acquire()
        return users
    }


//    override suspend fun getAppliances(): Single<ArrayList<Appliance>> = Single.fromCallable {
//        val semaphore = Semaphore(0)
//        val appliances = ArrayList<Appliance>()
//        cloudFirestore.collection("users").get().addOnSuccessListener { result ->
//            for (doc: QueryDocumentSnapshot in result) {
//                appliances.add(doc.toObject(Appliance::class.java))
//            }
//            semaphore.release();
//        }
//        semaphore.acquire()
//        return@fromCallable appliances
//    }.subscribeOn(Schedulers.io())

    override suspend fun getAppliances(): List<Appliance> {
        val semaphore = Semaphore(0)
        val appliances = ArrayList<Appliance>()
        cloudFirestore.collection("appliances").get().addOnSuccessListener { result ->
            for (doc: QueryDocumentSnapshot in result) {
                appliances.add(doc.toObject(Appliance::class.java))
            }
            semaphore.release();
        }
        semaphore.acquire()
        return appliances
    }



    override suspend fun addUser(user: FirebaseUser) {
        //val firebaseUser = ru.students.dvfu.mvp.model.userdata.FirebaseUser(user.displayName, user.email, user.photoUrl.toString(), "new_user")
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

//        val firebaseUser = ru.students.dvfu.mvp.model.userdata.FirebaseUser(user.displayName!!, user.email!!, user.photoUrl!!.toString(), "Logged user")
//        realtimeDatabase.reference.child("users").child(user.uid).setValue(firebaseUser)
    }

    override suspend fun addAppliance(appliance: Appliance) {
        TODO("Not yet implemented")
    }

//    fun isUserInDatabase(UID: String): Boolean {
//        return false
//        TODO("Not yet implemented")
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
//    }
}
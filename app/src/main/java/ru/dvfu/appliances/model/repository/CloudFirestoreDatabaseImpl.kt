package ru.dvfu.appliances.model.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.ui.Progress

class CloudFirestoreDatabaseImpl() : Repository {

    private val realtimeDatabase by lazy {
        //FirebaseDatabase.getInstance().reference
        FirebaseDatabase.getInstance("https://schedule-4c151-default-rtdb.europe-west1.firebasedatabase.app/")
    }
    private val cloudFirestore by lazy { FirebaseFirestore.getInstance() }
    private var TAG = "FirestoreDatabase"


    @ExperimentalCoroutinesApi
    override suspend fun addNewUser(user: User): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())
        if (user.isAnonymous) {
            flow.emit(Progress.Complete)
        } else {
            getUsersCollection().document(user.userId).set(user)
                .addOnCompleteListener {
                    flow.tryEmit(Progress.Complete)
                }
        }
        return flow
    }

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

    @ExperimentalCoroutinesApi
    override suspend fun getUsers() = channelFlow {
        val listeners = mutableListOf<ListenerRegistration>()
        listeners.add(cloudFirestore.collection("users").addSnapshotListener(getUsersSuccessListener(this)))
        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun getUsersSuccessListener(scope: ProducerScope<List<User>>): EventListener<QuerySnapshot> =
        EventListener<QuerySnapshot> { snapshots, error ->
            if (error != null) {
                Log.d("Schedule", "Get all users listener error", error)
                return@EventListener
            }

            if (snapshots != null) {
                val users = snapshots.toObjects(User::class.java)
                scope.trySend(users)
            }
        }

    @ExperimentalCoroutinesApi
    override suspend fun getAppliances() = channelFlow {
        val listeners = mutableListOf<ListenerRegistration>()
        listeners.add(cloudFirestore.collection("appliances").addSnapshotListener(getAppliancesSuccessListener(this)))
        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun getAppliancesSuccessListener(scope: ProducerScope<List<Appliance>>): EventListener<QuerySnapshot> =
        EventListener<QuerySnapshot> { snapshots, error ->
            if (error != null) {
                Log.d("Schedule", "Get all appliances listener error", error)
                return@EventListener
            }

            if (snapshots != null) {
                val appliances = snapshots.toObjects(Appliance::class.java)
                scope.trySend(appliances)
            }
        }


    override suspend fun addUser(user: User) {
        //val firebaseUser = ru.students.dvfu.mvp.model.userdata.FirebaseUser(user.displayName, user.email, user.photoUrl.toString(), "new_user")
        /*cloudFirestore.collection("users").document(user.uid).set(
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
        }*/

//        val firebaseUser = ru.students.dvfu.mvp.model.userdata.FirebaseUser(user.displayName!!, user.email!!, user.photoUrl!!.toString(), "Logged user")
//        realtimeDatabase.reference.child("users").child(user.uid).setValue(firebaseUser)
    }

    override suspend fun addAppliance(appliance: Appliance) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAppliance(appliance: Appliance) {
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
    private fun getUsersCollection(): CollectionReference {
        return cloudFirestore.collection(USERS_COLLECTION)
    }

    companion object {
        private const val USERS_COLLECTION = "users"
    }
}
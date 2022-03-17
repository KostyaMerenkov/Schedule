package ru.dvfu.appliances.model.datasource

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.RepositoryCollections
import ru.dvfu.appliances.ui.Progress

class AppliancesRepositoryImpl(
    private val dbCollections: RepositoryCollections,
) : AppliancesRepository {

    override suspend fun deleteUserFromAppliance(userToDelete: User, from: Appliance) {
        dbCollections.getAppliancesCollection().document(from.id)
            .update("userIds", from.userIds.filter { it != userToDelete.userId })
    }

    override suspend fun deleteSuperUserFromAppliance(userToDelete: User, from: Appliance) {
        dbCollections.getAppliancesCollection().document(from.id)
            .update("superuserIds", from.superuserIds.filter { it != userToDelete.userId })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getUserAppliances(userId: String) = channelFlow {
        val listeners = mutableListOf<ListenerRegistration>()

        listeners.add(
            dbCollections.getAppliancesCollection().whereArrayContains("userIds", userId)
                .addSnapshotListener(getUserAppliancesListener(this))
        )

        awaitClose { listeners.forEach { it.remove() } }

    }

    @ExperimentalCoroutinesApi
    private suspend fun getUserAppliancesListener(producerScope: ProducerScope<List<Appliance>>): EventListener<QuerySnapshot> =
        EventListener<QuerySnapshot> { snapshots, error ->
            if (error != null) {
                Log.d("Schedule", "Get all appliances listener error", error)
                return@EventListener
            }
            if (snapshots != null) {
                val appliances = snapshots.toObjects<Appliance>()
                producerScope.trySend(appliances)
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getSuperUserAppliances(userId: String) = channelFlow {
        val listeners = mutableListOf<ListenerRegistration>()

        listeners.add(
            dbCollections.getAppliancesCollection().whereArrayContains("superuserIds", userId)
                .addSnapshotListener(getSuperUserAppliancesListener(this))
        )

        awaitClose { listeners.forEach { it.remove() } }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getSuperUserAppliancesListener(producerScope: ProducerScope<List<Appliance>>): EventListener<QuerySnapshot> =
        EventListener<QuerySnapshot> { snapshots, error ->
            if (error != null) {
                Log.d("Schedule", "Get all appliances listener error", error)
                return@EventListener
            }
            if (snapshots != null) {
                val appliances = snapshots.toObjects<Appliance>()
                producerScope.trySend(appliances)
            }
        }

    override suspend fun addUsersToAppliance(
        appliance: Appliance,
        userIds: List<String>
    ): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())
        dbCollections.getAppliancesCollection().document(appliance.id).update(
            "userIds", userIds
        ).addOnCompleteListener {
            flow.tryEmit(Progress.Complete)
        }

        return flow
    }

    override suspend fun addSuperUsersToAppliance(
        appliance: Appliance,
        superuserIds: List<String>
    ): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())
        dbCollections.getAppliancesCollection().document(appliance.id).update(
            "superuserIds", superuserIds
        ).addOnCompleteListener {
            flow.tryEmit(Progress.Complete)
        }

        return flow
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getApplianceUsers(userIds: List<String>) = channelFlow<List<User>> {
        if (userIds.isNotEmpty()) {
            val listeners = mutableListOf<Task<QuerySnapshot>>()

            listeners.add(
                dbCollections.getUsersCollection().whereIn("userId", userIds).get()
                    .addOnCompleteListener(getApplianceUsersSuccessListener(this))
            )

            awaitClose {}
        } else send(listOf())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAppliance(applianceId: String) = channelFlow<Result<Appliance>> {
        val listeners = mutableListOf<ListenerRegistration>()
        listeners.add(
            dbCollections.getAppliancesCollection().document(applianceId)
                .addSnapshotListener(getApplianceSuccessListener(this))
        )
        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getApplianceSuccessListener(producerScope: ProducerScope<Result<Appliance>>)
    : EventListener<DocumentSnapshot> = EventListener<DocumentSnapshot> { document, error ->
            if (error != null) {
                Log.d("Schedule", "Get all appliances listener error", error)
                producerScope.trySend(Result.failure(error.fillInStackTrace()))
                return@EventListener
            }

            if (document != null) {
                val appliance = document.toObject<Appliance>()
                appliance?.let { producerScope.trySend(Result.success(appliance)) }
                    ?: producerScope.trySend(Result.failure(Throwable()))


            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getApplianceUsersSuccessListener(
        scope: ProducerScope<List<User>>,
    ): OnCompleteListener<QuerySnapshot> =
        OnCompleteListener<QuerySnapshot> { task ->
            if (task.exception != null) {
                Log.d("Schedule", "Get all users listener error", task.exception)
                return@OnCompleteListener
            }

            if (task.isSuccessful) {
                val users = task.result.toObjects(User::class.java)
                scope.trySend(users)
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAppliances() = channelFlow {
        val listeners = mutableListOf<ListenerRegistration>()
        listeners.add(
            dbCollections.getAppliancesCollection()
                .addSnapshotListener(getAppliancesSuccessListener(this))
        )
        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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


/*    override suspend fun addUser(user: User): StateFlow<Progress> {
        //val firebaseUser = ru.students.dvfu.mvp.model.userdata.FirebaseUser(user.displayName, user.email, user.photoUrl.toString(), "new_user")
        *//*cloudFirestore.collection("users").document(user.uid).set(
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
        }*//*

//        val firebaseUser = ru.students.dvfu.mvp.model.userdata.FirebaseUser(user.displayName!!, user.email!!, user.photoUrl!!.toString(), "Logged user")
//        realtimeDatabase.reference.child("users").child(user.uid).setValue(firebaseUser)
    }*/

    override suspend fun addAppliance(appliance: Appliance): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())
        dbCollections.getAppliancesCollection().document(appliance.id).set(appliance)
            .addOnCompleteListener {
                flow.tryEmit(Progress.Complete)
            }
        return flow
    }

    override suspend fun deleteAppliance(appliance: Appliance) {
        dbCollections.getAppliancesCollection().document(appliance.id).delete()
    }
}
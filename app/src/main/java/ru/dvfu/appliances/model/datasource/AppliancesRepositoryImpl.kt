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
import kotlinx.coroutines.flow.channelFlow
import ru.dvfu.appliances.compose.utils.NotificationManager
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.RepositoryCollections
import ru.dvfu.appliances.model.utils.suspendCoroutineWithTimeout
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppliancesRepositoryImpl(
    private val dbCollections: RepositoryCollections,
) : AppliancesRepository {

    override suspend fun deleteUserFromAppliance(userIdToDelete: String, from: Appliance) =
        suspendCoroutineWithTimeout { continuation ->
            dbCollections.getAppliancesCollection().document(from.id)
                .update("userIds", from.userIds.filter { it != userIdToDelete })
                .addOnCompleteListener(simpleOnCompleteListener(continuation))
        }

    override suspend fun deleteSuperUserFromAppliance(userIdToDelete: String, from: Appliance) =
        suspendCoroutine<Result<Unit>> { continuation ->
            dbCollections.getAppliancesCollection().document(from.id)
                .update("superuserIds", from.superuserIds.filter { it != userIdToDelete })
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else continuation.resume(Result.failure(it.exception ?: Throwable()))
                }
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
    ) = suspendCoroutineWithTimeout { continuation ->

        dbCollections.getAppliancesCollection().document(appliance.id).update(
            "userIds", userIds
        ).addOnCompleteListener(simpleOnCompleteListener(continuation))
    }

    override suspend fun addSuperUsersToAppliance(
        appliance: Appliance,
        superuserIds: List<String>
    ) = suspendCoroutineWithTimeout { continuation ->
        dbCollections.getAppliancesCollection().document(appliance.id).update(
            "superuserIds", superuserIds
        ).addOnCompleteListener(simpleOnCompleteListener(continuation))
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

    override suspend fun getAppliancesOneTime() =
        suspendCoroutine<Result<List<Appliance>>> { continuation ->
            dbCollections.getAppliancesCollection().get().addOnCompleteListener {
                if (it.isSuccessful) {
                    continuation.resume(Result.success(it.result.toObjects<Appliance>()))
                } else continuation.resume(Result.failure(it.exception ?: Throwable()))
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

    override suspend fun addAppliance(appliance: Appliance) =
        suspendCoroutineWithTimeout { continuation ->
            dbCollections.getAppliancesCollection().document(appliance.id).set(appliance)
                .addOnCompleteListener(simpleOnCompleteListener(continuation))
        }

    override suspend fun deleteAppliance(applianceId: String) =
        suspendCoroutineWithTimeout(Duration.ofMinutes(1).toMillis()) { continuation ->
            dbCollections.getAppliancesCollection().document(applianceId).delete()
                .addOnCompleteListener(simpleOnCompleteListener(continuation))
        }
}
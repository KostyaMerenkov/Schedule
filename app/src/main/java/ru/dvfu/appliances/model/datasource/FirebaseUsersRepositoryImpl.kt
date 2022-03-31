package ru.dvfu.appliances.model.datasource

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import ru.dvfu.appliances.model.datastore.UserDatastore
import ru.dvfu.appliances.model.repository.UsersRepository
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.Roles
import ru.dvfu.appliances.model.utils.RepositoryCollections
import ru.dvfu.appliances.ui.Progress

class FirebaseUsersRepositoryImpl(
    private val context: Context,
    private val dbCollections: RepositoryCollections,
    private val userDatastore: UserDatastore
) : UsersRepository {

    private val fireBaseAuth = FirebaseAuth.getInstance()

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getUsers() = channelFlow {
        val listeners = mutableListOf<ListenerRegistration>()
        listeners.add(
            dbCollections.getUsersCollection()
                .addSnapshotListener(getUsersSuccessListener(this))
        )
        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val authListener = FirebaseAuth.AuthStateListener {
                runBlocking {
                    send(it.currentUser?.run { mapFirebaseUserToUser(this) })
                }
            }

            fireBaseAuth.addAuthStateListener(authListener)
            awaitClose { fireBaseAuth.removeAuthStateListener(authListener) }
        }

    override suspend fun addNewUser(user: User): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())

        val userFromDatabase =
            dbCollections.getUsersCollection().document(user.userId).get().await()
                .toObject(User::class.java)

        if (userFromDatabase != null) {
            /*val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Google")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)*/
            userDatastore.saveUser(userFromDatabase)
            flow.tryEmit(Progress.Complete)
        } else {
            dbCollections.getUsersCollection().document(user.userId).set(user)
                .addOnCompleteListener {

                    runBlocking {
                        userDatastore.saveUser(user)
                    }
                    flow.tryEmit(Progress.Complete)
                }

        }
        return flow
    }

    override suspend fun setUserListener(user: User) {
        val listeners = mutableListOf<ListenerRegistration>()

        listeners.add(
            dbCollections.getUsersCollection().document(user.userId)
                .addSnapshotListener(getUserSnapshotListener())
        )
    }

    private fun getUserSnapshotListener(): EventListener<DocumentSnapshot> =
        EventListener { snapshot, error ->
            if (error != null) {
                Log.d("Schedule", "User snapshot listener", error)
                return@EventListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("Schedule", "Current data: ${snapshot.data}")
                snapshot.toObject(User::class.java)?.let { userToUpdate ->
                    runBlocking {
                        userDatastore.saveUser(userToUpdate)
                    }
                }
            } else {
                Log.d("Fishing", "Current data: null")
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUserFromDB: Flow<User>
        get() = callbackFlow {
            val listeners = mutableListOf<ListenerRegistration>()

            val userId = fireBaseAuth.currentUser?.uid
            userId?.let { id ->
                listeners.add(
                    dbCollections.getUsersCollection().document(id)
                        .addSnapshotListener(getUserListener(this))
                )
            }
            awaitClose { listeners.remove(listeners.first()) }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getUserListener(scope: ProducerScope<User>) =
        EventListener<DocumentSnapshot> { snapshot, error ->
            if (error != null) {
                Log.d("Schedule", "User snapshot listener", error)
                return@EventListener
            } else {
                val user = snapshot?.toObject<User>()
                scope.trySend(user ?: User())
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun logoutCurrentUser() = callbackFlow {
        AuthUI.getInstance().signOut(context).addOnSuccessListener {
            trySend(true)
        }
        awaitClose {}
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getUser(userId: String) = callbackFlow<Result<User>> {
        val doc = dbCollections.getUsersCollection().document(userId).get().await()
        val user = doc.toObject<User>()
        user?.let {
            trySend(Result.success(it))
        } ?: run {
            trySend(Result.failure(Throwable()))
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getUser(scope: ProducerScope<Result<User>>) = OnCompleteListener<DocumentSnapshot> {
        if (it.isSuccessful) {
            val user = it.result.toObject<User>()
            user?.let { scope.trySend(Result.success(it)) } ?: scope.trySend(
                Result.failure(Throwable())
            )
        } else {
            scope.trySend(Result.failure(Throwable()))
        }
    }

    override suspend fun updateUserField(userId: String, data: Map<String, Any>) {
        dbCollections.getUsersCollection().document(userId).update(data).addOnCompleteListener {

        }
    }

    private fun mapFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        return with(firebaseUser) {
            User(
                uid,
                displayName ?: "Anonymous",
                email ?: "",
                Roles.GUEST.ordinal,
                isAnonymous,
                photoUrl?.toString() ?: "",
            )
        }
        //TODO("change name")
    }

}
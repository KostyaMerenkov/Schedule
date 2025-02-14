package ru.dvfu.appliances.model.datasource

import android.content.Context
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
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
import ru.dvfu.appliances.model.utils.Constants
import ru.dvfu.appliances.model.utils.RepositoryCollections
import ru.dvfu.appliances.model.utils.suspendCoroutineWithTimeout
import ru.dvfu.appliances.ui.Progress
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
                runBlocking { send(it.currentUser?.run { mapFirebaseUserToUser(this) }) }
            }

            fireBaseAuth.addAuthStateListener(authListener)
            awaitClose { fireBaseAuth.removeAuthStateListener(authListener) }
        }

    override suspend fun addNewUser(user: User): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())

        if (user.anonymous) {
            userDatastore.saveUser(user)
            flow.emit(Progress.Complete)
        } else {

            val userFromDatabase =
                dbCollections.getUsersCollection().document(user.userId).get().await()
                    .toObject(User::class.java)

            if (userFromDatabase != null) {
                /*val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Google")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)*/
                userDatastore.saveUser(userFromDatabase)
                uploadMessagingToken(userId = user.userId)
                flow.tryEmit(Progress.Complete)
            } else {
                dbCollections.getUsersCollection().document(user.userId).set(user)
                    .addOnCompleteListener {
                        runBlocking {
                            userDatastore.saveUser(user)
                            uploadMessagingToken(userId = user.userId)
                            flow.tryEmit(Progress.Complete)
                        }
                    }
            }
        }

        return flow
    }

    private suspend fun uploadMessagingToken(userId: String) =
        suspendCoroutine<Result<Unit>> { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        Constants.TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    continuation.resume(Result.failure(task.exception ?: Throwable()))
                }

                // Get new FCM registration token
                val token = task.result
                dbCollections.getUsersCollection().document(userId).update("msgToken", token)
                    .addOnCompleteListener {
                        continuation.resume(Result.success(Unit))
                    }
            }
        }

    override suspend fun setUserListener(user: User) {
        val listeners = mutableListOf<ListenerRegistration>()

        listeners.add(
            dbCollections.getUsersCollection().document(user.userId)
                .addSnapshotListener(getUserSnapshotListener())
        )
    }

    override suspend fun setNewProfileData(
        userId: String,
        data: Map<String, Any>
    ): Result<Unit> = suspendCoroutine {
        dbCollections.getUsersCollection().document(userId).update(data)
        it.resume(Result.success(Unit))
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
                Log.d("Schedule", "Current data: null")
            }
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
        //userDatastore.saveUser(User())
        AuthUI.getInstance().signOut(context).addOnSuccessListener {
            trySend(true)
        }
        awaitClose {}
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getUser(userId: String) = suspendCoroutine<Result<User>> { continuation ->
        val doc = dbCollections.getUsersCollection().document(userId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject<User>()
                user?.let {
                    continuation.resume(Result.success(it))
                } ?: run {
                    continuation.resume(Result.failure(it.exception ?: Throwable()))
                }
            } else continuation.resume(Result.failure(it.exception ?: Throwable()))
        }
    }

    override suspend fun updateUserField(userId: String, data: Map<String, Any>) =
        suspendCoroutineWithTimeout { continuation ->
            dbCollections.getUsersCollection().document(userId).update(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else continuation.resume(Result.failure(it.exception ?: Throwable()))
                }
        }

    override suspend fun updateCurrentUserField(data: Map<String, Any>) {
        dbCollections.getUsersCollection()
            .document(userDatastore.getCurrentUser.first().userId)
            .update(data)
    }

    private fun mapFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        return with(firebaseUser) {
            User(
                userId = uid,
                userName = displayName ?: "Аноним",
                email = email ?: "",
                role = Roles.GUEST.ordinal,
                anonymous = isAnonymous,
                userPic = photoUrl?.toString() ?: "",
            )
        }
    }

}
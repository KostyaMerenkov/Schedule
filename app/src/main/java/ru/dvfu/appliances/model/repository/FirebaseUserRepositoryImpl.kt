package ru.dvfu.appliances.model.repository

import android.content.Context
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository.entity.Role
import ru.dvfu.appliances.ui.Progress

class FirebaseUserRepositoryImpl(private val context: Context) : UserRepository {

    private val fireBaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    @ExperimentalCoroutinesApi
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

    @ExperimentalCoroutinesApi
    override val currentUserFromDB: Flow<User>
        get() = callbackFlow {
            val listeners = mutableListOf<ListenerRegistration>()

            val userId = fireBaseAuth.currentUser?.uid
            userId?.let { id ->
                listeners.add(
                    getUsersCollection().document(id).addSnapshotListener(getUser(this))
                )
            }
            awaitClose { listeners.remove(listeners.first()) }
        }

    @ExperimentalCoroutinesApi
    private fun getUser(scope: ProducerScope<User>) =
        EventListener<DocumentSnapshot> { snapshot, error ->
            if (error != null) {
                Log.d("Schedule", "User snapshot listener", error)
                return@EventListener
            }
            scope.trySend(snapshot?.toObject(User::class.java) ?: User(appliances = listOf()))

        }

    @ExperimentalCoroutinesApi
    override suspend fun logoutCurrentUser() = callbackFlow {
        AuthUI.getInstance().signOut(context).addOnSuccessListener {
            trySend(true)
        }
        awaitClose {}
    }

    @ExperimentalCoroutinesApi
    override suspend fun addNewUser(user: User): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())
        if (user.isAnonymous) {
            flow.emit(Progress.Complete)
        } else {
            getUsersCollection().document(user.userId).get().addOnCompleteListener {
                if (it.result.exists()) flow.tryEmit(Progress.Complete)
                else {
                    getUsersCollection().document(user.userId).set(user)
                        .addOnCompleteListener {
                            flow.tryEmit(Progress.Complete)
                        }
                }
            }
        }
        return flow
    }

    @ExperimentalCoroutinesApi
    override suspend fun getUserWithId(userId: String) = callbackFlow {
            val listeners = mutableListOf<ListenerRegistration>()

                listeners.add(
                    getUsersCollection().document(userId).addSnapshotListener(getUser(this))
                )
            awaitClose { listeners.remove(listeners.first()) }
    }

    private fun mapFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        return with(firebaseUser) {
            User(
                uid,
                displayName ?: "Anonymous",
                email ?: "",
                Role.GUEST.ordinal,
                isAnonymous,
                photoUrl?.toString() ?: "",
                listOf()
            )
        }
        //TODO("change name")
    }

    private fun getUsersCollection(): CollectionReference {
        return db.collection(USERS_COLLECTION)
    }

    companion object {
        private const val USERS_COLLECTION = "users"
    }
}
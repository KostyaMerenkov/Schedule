package ru.dvfu.appliances.model.auth

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.userdata.entities.Role

class FirebaseAuthManagerImpl(private val context: Context) : AuthManager {

    private val fireBaseAuth = FirebaseAuth.getInstance()

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
    override suspend fun logoutCurrentUser() = callbackFlow {
        AuthUI.getInstance().signOut(context).addOnSuccessListener {
            trySend(true)
        }
        awaitClose{}
    }

    private fun mapFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        return with(firebaseUser) {
            User(
                uid,
                displayName ?: "Anonymous",
                email!!,
                Role.GUEST.ordinal,
                isAnonymous,
                photoUrl?.toString() ?: ""
            )
        }
        //TODO("change name")
    }
}
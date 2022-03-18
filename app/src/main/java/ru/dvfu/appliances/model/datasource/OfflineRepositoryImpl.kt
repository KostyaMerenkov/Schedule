package ru.dvfu.appliances.model.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.RepositoryCollections

class OfflineRepositoryImpl(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val dbCollections: RepositoryCollections,
) : OfflineRepository {

    override fun getUser(userId: String) = callbackFlow<Result<User>> {
        db.disableNetwork().addOnSuccessListener {
            dbCollections.getUsersCollection().document(userId).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.toObject<User>()
                    user?.let { trySend(Result.success(user)) } ?: trySend(Result.failure(Throwable()))
                } else trySend(Result.failure(it.exception ?: Throwable()))
                db.enableNetwork()
            }
        }
        awaitClose { }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAppliances() = callbackFlow<List<Appliance>> {
        db.disableNetwork().addOnSuccessListener {
            dbCollections.getAppliancesCollection().get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val appliances = it.result.toObjects(Appliance::class.java)
                    trySend(appliances)
                } else trySend(listOf())
                db.enableNetwork()
            }
        }
        awaitClose { }
    }

    override fun getApplianceById(applianceId: String) = callbackFlow<Result<Appliance>> {
        db.disableNetwork().addOnSuccessListener {
            dbCollections.getAppliancesCollection().document(applianceId).get().addOnCompleteListener {
                db.enableNetwork()
                if (it.isSuccessful) {
                    val appliance = it.result.toObject(Appliance::class.java)
                    appliance?.let {
                        trySend(Result.success(appliance))
                    } ?: trySend(Result.failure(it.exception ?: Throwable()))

                } else {
                    trySend(Result.failure(it.exception ?: Throwable()))
                }
            }
        }
        awaitClose { }
    }
}
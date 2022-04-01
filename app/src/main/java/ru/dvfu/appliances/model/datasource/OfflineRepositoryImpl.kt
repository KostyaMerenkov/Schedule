package ru.dvfu.appliances.model.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.asTask
import kotlinx.coroutines.tasks.await
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.RepositoryCollections

class OfflineRepositoryImpl(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val dbCollections: RepositoryCollections,
) : OfflineRepository {

    override suspend fun getUser(userId: String) = flow {
        db.disableNetwork().await()
        val doc = dbCollections.getUsersCollection().document(userId).get().await()
        val user = doc.toObject<User>()
        user?.let { emit(Result.success(user)) } ?: emit(Result.failure(Throwable()))
        db.enableNetwork()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAppliances() = flow {
        db.disableNetwork().await()
        val collection = dbCollections.getAppliancesCollection().get().await()
        val appliances = collection.toObjects<Appliance>()
        emit(appliances)
        db.enableNetwork()
    }

    override fun getApplianceById(applianceId: String) = callbackFlow<Result<Appliance>> {
        db.disableNetwork().addOnSuccessListener {
            dbCollections.getAppliancesCollection().document(applianceId).get()
                .addOnCompleteListener {
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
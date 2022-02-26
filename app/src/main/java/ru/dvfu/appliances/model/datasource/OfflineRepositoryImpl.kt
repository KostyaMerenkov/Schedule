package ru.dvfu.appliances.model.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.RepositoryCollections

class OfflineRepositoryImpl(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val dbCollections: RepositoryCollections,
) : OfflineRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAppliances() = callbackFlow<List<Appliance>> {
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

    override suspend fun getApplianceById() {
        TODO("Not yet implemented")
    }
}
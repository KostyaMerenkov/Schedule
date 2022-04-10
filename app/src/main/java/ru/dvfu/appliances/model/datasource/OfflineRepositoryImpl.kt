package ru.dvfu.appliances.model.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
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
import kotlin.Exception

class OfflineRepositoryImpl(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val dbCollections: RepositoryCollections,
) : OfflineRepository {

    override suspend fun getUser(userId: String) = flow {
        val doc = dbCollections.getUsersCollection().document(userId).get(Source.CACHE).await()
        val user = doc.toObject<User>()
        user?.let { emit(Result.success(user)) } ?: emit(Result.failure(Throwable()))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAppliances() = flow {
        val collection = dbCollections.getAppliancesCollection().get(Source.CACHE).await()
        val appliances = collection.toObjects<Appliance>()
        if (appliances.isNullOrEmpty()) {
            emit(Result.failure(Throwable()))
        } else {
            emit(Result.success(appliances))
        }
    }

    override fun getApplianceById(applianceId: String) = flow<Result<Appliance>> {
        val doc =
            dbCollections.getAppliancesCollection().document(applianceId).get(Source.CACHE).await()
        doc.toObject<Appliance>()?.let {
            emit(Result.success(it))
        } ?: kotlin.run {
            emit(Result.failure(Throwable()))
        }

    }


}
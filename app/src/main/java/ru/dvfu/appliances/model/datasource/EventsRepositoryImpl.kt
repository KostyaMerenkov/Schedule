package ru.dvfu.appliances.model.datasource

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.utils.RepositoryCollections
import ru.dvfu.appliances.ui.Progress
import java.time.LocalDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EventsRepositoryImpl(
    private val dbCollections: RepositoryCollections,
) : EventsRepository {

    private var TAG = "EventsFirestoreDatabase"


    override suspend fun addNewEvent(event: Event) = suspendCoroutine<Result<Unit>> { continuation ->
        dbCollections.getEventsCollection().document(event.id).set(event).addOnCompleteListener {
            if (it.isSuccessful) continuation.resume(Result.success(Unit))
            else continuation.resume(Result.failure(it.exception ?: Throwable()))
        }
    }

    override suspend fun getAllEventsFromDate(date: Long): Flow<List<Event>> = channelFlow {
        val listeners = mutableListOf<ListenerRegistration>()
        listeners.add(
            dbCollections.getEventsCollection()
                .orderBy("timeStart").whereGreaterThan("timeStart", date)
                .addSnapshotListener(getEventsSuccessListener(this))
        )
        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    override suspend fun deleteEvent(id: String) = suspendCoroutine<Result<Unit>> { continuation ->
        dbCollections.getEventsCollection().document(id).delete().addOnCompleteListener {
            if (it.isSuccessful) continuation.resume(Result.success(Unit))
            else continuation.resume(Result.failure(it.exception ?: Throwable()))
        }
    }

    override suspend fun setNewTimeEnd(eventId: String, timeEnd: Long) =
        suspendCoroutine<Result<Unit>> { continuation ->
            dbCollections.getEventsCollection().document(eventId).update("timeEnd", timeEnd)
                .addOnCompleteListener {
                    if (it.isSuccessful) continuation.resume(Result.success(Unit))
                    else continuation.resume(Result.failure(it.exception ?: Throwable()))
                }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getEventsSuccessListener(scope: ProducerScope<List<Event>>): EventListener<QuerySnapshot> =
        EventListener<QuerySnapshot> { snapshots, error ->
            if (error != null) {
                Log.d("Schedule", "Get all events listener error", error)
                return@EventListener
            }

            if (snapshots != null) {
                val events = snapshots.toObjects(Event::class.java)
                scope.trySend(events)
            }
        }

    override suspend fun getApplianceEventsAfterTime(applianceId: String, time: Long) =
        suspendCoroutine<Result<List<Event>>> { continuation ->
            dbCollections.getEventsCollection()
                .whereEqualTo("applianceId", applianceId)
                .whereGreaterThan("timeEnd", time)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val events = it.result.toObjects<Event>()
                        continuation.resume(Result.success(events))
                    }
                    else continuation.resume(Result.failure(it.exception ?: Throwable()))
                }
        }

}

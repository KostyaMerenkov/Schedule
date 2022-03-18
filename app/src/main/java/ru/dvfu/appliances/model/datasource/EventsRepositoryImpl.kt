package ru.dvfu.appliances.model.datasource

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import ru.dvfu.appliances.model.repository.EventsRepository
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.utils.RepositoryCollections
import ru.dvfu.appliances.ui.Progress

class EventsRepositoryImpl(
    private val dbCollections: RepositoryCollections,
) : EventsRepository {

    private var TAG = "EventsFirestoreDatabase"


    override suspend fun addNewEvent(event: Event): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())

        dbCollections.getEventsCollection().document(event.id).set(event).addOnCompleteListener {
            flow.tryEmit(Progress.Complete)
        }

        return flow
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

    override suspend fun deleteEvent(id: String) = callbackFlow<Result<Unit>> {

        dbCollections.getEventsCollection().document(id).delete().addOnCompleteListener{
            if (it.isSuccessful)  trySend(Result.success(Unit))
            else trySend(Result.failure(it.exception ?: Throwable()))
        }

        awaitClose {  }
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
}

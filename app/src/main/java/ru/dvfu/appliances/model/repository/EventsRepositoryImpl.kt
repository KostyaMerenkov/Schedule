package ru.dvfu.appliances.model.repository

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Event
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.ui.Progress

class EventsRepositoryImpl() : EventsRepository {

    private val cloudFirestore by lazy { FirebaseFirestore.getInstance() }
    private var TAG = "EventsFirestoreDatabase"

    private fun getEventsCollection(): CollectionReference {
        return cloudFirestore.collection(EVENTS_COLLECTION)
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val APPLIANCES_COLLECTION = "appliances"
        private const val EVENTS_COLLECTION = "events"
    }
    override suspend fun addNewEvent(event: Event): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())

        getEventsCollection().document(event.id).set(event).addOnCompleteListener {
            flow.tryEmit(Progress.Complete)
        }

        return flow
    }

    override suspend fun getAllEventsFromDate(date: Long): Flow<List<Event>>
            = channelFlow {
        val listeners = mutableListOf<ListenerRegistration>()
        listeners.add(
            getEventsCollection().orderBy("timeStart").whereGreaterThan("timeStart", date)
                .addSnapshotListener(getEventsSuccessListener(this))
        )
        awaitClose {
            listeners.forEach { it.remove() }
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
}

package ru.dvfu.appliances.model.datasource.deprecated

import com.google.firebase.database.FirebaseDatabase
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.utils.RepositoryCollections

class CloudFirestoreDatabaseImpl(
    private val dbCollections: RepositoryCollections,
) : Repository {

    private val realtimeDatabase by lazy {
        //FirebaseDatabase.getInstance().reference
        FirebaseDatabase.getInstance("https://schedule-4c151-default-rtdb.europe-west1.firebasedatabase.app/")
    }
    private var TAG = "FirestoreDatabase"

    /*@OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun addNewUser(user: User): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())
        if (user.isAnonymous) {
            flow.emit(Progress.Complete)
        } else {
            dbCollections.getUsersCollection().document(user.userId).set(user)
                .addOnCompleteListener {
                    flow.tryEmit(Progress.Complete)
                }
        }
        return flow
    }*/

    //    fun isUserInDatabase(UID: String): Boolean {
//        return false
//        TODO("Not yet implemented")
//        return if (cloudFirestore.collection("users").document(UID).get().addOnCompleteListener {
//            return it.isSuccessful
//            }
//                .isComplete) {
//            Log.d(TAG, "User $UID is already in database")
//            true
//        } else {
//            Log.d(TAG, "User $UID is not in database")
//            false
//        }
//    }

    /*override suspend fun addNewEvent(calendarEvent: CalendarEvent): StateFlow<Progress> {
        val flow = MutableStateFlow<Progress>(Progress.Loading())

        dbCollections.getEventsCollection().document(calendarEvent.id).set(calendarEvent).addOnCompleteListener {
            flow.tryEmit(Progress.Complete)
        }

        return flow
    }

    override suspend fun getAllEventsFromDate(date: Long): Flow<List<CalendarEvent>>
    = channelFlow {
        val listeners = mutableListOf<ListenerRegistration>()
        listeners.add(
            dbCollections.getEventsCollection().orderBy("timeStart").whereGreaterThan("timeStart", date)
                .addSnapshotListener(getEventsSuccessListener(this))
        )
        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getEventsSuccessListener(scope: ProducerScope<List<CalendarEvent>>): EventListener<QuerySnapshot> =
        EventListener<QuerySnapshot> { snapshots, error ->
            if (error != null) {
                Log.d("Schedule", "Get all events listener error", error)
                return@EventListener
            }

            if (snapshots != null) {
                val events = snapshots.toObjects(CalendarEvent::class.java)
                scope.trySend(events)
            }
        }*/
}

package ru.dvfu.appliances.model.utils

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RepositoryCollections(val cloudFirestore: FirebaseFirestore = Firebase.firestore) {

    init {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        cloudFirestore.firestoreSettings = settings
    }

    fun getUsersCollection(): CollectionReference {
        return cloudFirestore.collection(USERS_COLLECTION)
    }

    fun getAppliancesCollection(): CollectionReference {
        return cloudFirestore.collection(APPLIANCES_COLLECTION)
    }

    fun getEventsCollection(): CollectionReference {
        return cloudFirestore.collection(EVENTS_COLLECTION)
    }

    fun getBookingCollection(): CollectionReference {
        return cloudFirestore.collection(BOOKING_COLLECTION)
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val APPLIANCES_COLLECTION = "appliances"
        private const val BOOKING_COLLECTION = "booking"
        private const val EVENTS_COLLECTION = "events"
    }

}
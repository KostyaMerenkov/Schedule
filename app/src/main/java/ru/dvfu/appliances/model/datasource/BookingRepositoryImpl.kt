package ru.dvfu.appliances.model.datasource

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.BookingRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.Booking
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.RepositoryCollections
import ru.dvfu.appliances.ui.Progress
import kotlin.coroutines.suspendCoroutine

class BookingRepositoryImpl(
    private val dbCollections: RepositoryCollections,
) : BookingRepository {

    override suspend fun getAllUserBooking(userId: String) = flow<Result<List<Booking>>> {
        val results = dbCollections.getBookingCollection().whereEqualTo("userId", userId).get().await()
        val bookingList = results.toObjects(Booking::class.java)
        emit(Result.success(bookingList))
    }

    override suspend fun createBooking(booking: Booking) {
        dbCollections.getBookingCollection().document(booking.id).set(booking)
    }

    override suspend fun approveBooking(booking: Booking, managedById: String, managerCommentary: String) {
        dbCollections.getBookingCollection().document(booking.id).set(
            booking.copy(
                managedById = managedById,
                managerCommentary = managerCommentary,
                status = BookingStatus.APPROVED
            )
        )
    }

    override suspend fun declineBooking(booking: Booking, managedById: String, managerCommentary: String) {
        dbCollections.getBookingCollection().document(booking.id).set(
            booking.copy(
                managedById = managedById,
                managerCommentary = managerCommentary,
                status = BookingStatus.DECLINED
            )
        )
    }


    override suspend fun deleteBooking(bookingId: String) {
        dbCollections.getBookingCollection().document(bookingId).delete()
    }
}
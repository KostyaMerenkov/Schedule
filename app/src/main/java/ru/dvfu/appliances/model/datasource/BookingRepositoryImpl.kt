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
import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.model.repository.AppliancesRepository
import ru.dvfu.appliances.model.repository.BookingRepository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.model.utils.RepositoryCollections
import ru.dvfu.appliances.ui.Progress
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BookingRepositoryImpl(
    private val dbCollections: RepositoryCollections,
) : BookingRepository {

    /*override suspend fun getAllUserBooking(userId: String) = flow<Result<List<Booking>>> {
        val results =
            dbCollections.getBookingCollection().whereEqualTo("userId", userId).get().await()
        val bookingList = results.toObjects(Booking::class.java)
        emit(Result.success(bookingList))
    }

    override suspend fun getAllBooking() = flow<Result<List<Booking>>> {
        val results = dbCollections.getBookingCollection().get().await()

        val bookingList = results.toObjects(Booking::class.java)
        emit(Result.success(bookingList))

    }

    *//*override suspend fun getAllBooking() = channelFlow<Result<List<Booking>>> {
        dbCollections.getBookingCollection().get().addOnSuccessListener {
            val bookingList = it.toObjects(Booking::class.java)
            trySend(Result.success(bookingList))
        }.addOnFailureListener {
            trySend(Result.failure(it ?: Throwable()))
        }

        awaitClose()
    }*//*

    override suspend fun createBooking(booking: Booking) =
        suspendCoroutine<Result<Unit>> { continuation ->
            dbCollections.getBookingCollection().document(booking.id).set(booking)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else continuation.resume(Result.failure(it.exception ?: Throwable()))
                }
        }

    override suspend fun approveBooking(
        bookId: String,
        managedById: String,
        managerCommentary: String, managedTime: Long,
    ): Result<Unit> = suspendCoroutine { continuation ->
        dbCollections.getBookingCollection().document(bookId).update(
            "managedById", managedById,
            "managerCommentary", managerCommentary,
            "managedTime", managedTime,
            "status", BookingStatus.APPROVED
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                continuation.resume(Result.success(Unit))
            } else continuation.resume(Result.failure(it.exception ?: Throwable()))
        }
    }

    override suspend fun declineBooking(
        bookId: String,
        managedById: String,
        managerCommentary: String, managedTime: Long,
    ): Result<Unit> = suspendCoroutine { continuation ->
        dbCollections.getBookingCollection().document(bookId).update(
            "managedById", managedById,
            "managerCommentary", managerCommentary,
            "managedTime", managedTime,
            "status", BookingStatus.DECLINED
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                continuation.resume(Result.success(Unit))
            } else continuation.resume(Result.failure(it.exception ?: Throwable()))
        }

    }


    override suspend fun deleteBooking(bookingId: String): Result<Unit> =
        suspendCoroutine { continuation ->
            dbCollections.getBookingCollection().document(bookingId).delete()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else continuation.resume(Result.failure(it.exception ?: Throwable()))
                }
        }*/
}
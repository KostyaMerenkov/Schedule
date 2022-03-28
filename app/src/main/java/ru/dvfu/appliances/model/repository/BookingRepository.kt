package ru.dvfu.appliances.model.repository

import kotlinx.coroutines.flow.Flow
import ru.dvfu.appliances.model.repository.entity.Booking

interface BookingRepository {

    suspend fun getAllUserBooking(userId: String): Flow<Result<List<Booking>>>

    suspend fun createBooking(booking: Booking)

    suspend fun approveBooking(booking: Booking, managedById: String, managerCommentary: String)
    suspend fun declineBooking(booking: Booking, managedById: String, managerCommentary: String)

    suspend fun deleteBooking(bookingId: String)
}
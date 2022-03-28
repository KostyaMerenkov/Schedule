package ru.dvfu.appliances.model.repository

import ru.dvfu.appliances.model.repository.entity.Booking

interface BookingRepository {

    suspend fun createBooking(booking: Booking)

    suspend fun approveBooking(booking: Booking, managedById: String, managerCommentary: String)
    suspend fun declineBooking(booking: Booking, managedById: String, managerCommentary: String)

    suspend fun deleteBooking(bookingId: String)
}
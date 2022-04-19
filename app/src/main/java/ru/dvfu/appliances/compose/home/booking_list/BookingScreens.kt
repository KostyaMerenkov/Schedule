/*
package ru.dvfu.appliances.compose.home.booking_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.dvfu.appliances.compose.home.BookingListViewModel
import ru.dvfu.appliances.compose.home.NoBookingsView
import ru.dvfu.appliances.compose.utils.toMillis
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.UiBooking
import java.util.*

@Composable
fun PendingBookingsList(
    bookings: List<UiBooking>,
    viewModel: BookingListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings.filter { it.status == BookingStatus.NONE }.let {
            if (it.isEmpty()) {
                item { NoBookingsView() }
            } else {
                items(count = it.size) { index ->
                    BookingRequestItemView(
                        booking = it[index],
                        onApproveClick = { viewModel.approveBook(it[index]) },
                        onDeclineClick = { viewModel.declineBook(it[index]) }
                    )
                }
            }
        }
    }
}

@Composable
fun ApprovedBookingsList(
    bookings: List<UiBooking>,
    viewModel: BookingListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings.filter { it.status == BookingStatus.APPROVED && (it.timeEnd.toMillis >= Date().time) }
            .let {
                if (it.isEmpty()) {
                    item { NoBookingsView() }
                } else {
                    items(count = bookings.size) { index ->
                        BookingRequestItemView(
                            booking = bookings[index],
                            onApproveClick = { viewModel.approveBook(it[index]) },
                            onDeclineClick = { viewModel.declineBook(it[index]) }
                        )
                    }
                }
            }
    }
}

@Composable
fun DeclinedAndPastBookingsList(
    bookings: List<UiBooking>,
    viewModel: BookingListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings.filter { it.status == BookingStatus.DECLINED && (it.timeEnd.toMillis < Date().time) }
            .let {
                if (it.isEmpty()) {
                    item { NoBookingsView() }
                } else {
                    items(count = bookings.size) { index ->
                        BookingRequestItemView(
                            booking = bookings[index],
                            onApproveClick = { viewModel.approveBook(it[index]) },
                            onDeclineClick = { viewModel.declineBook(it[index]) }
                        )
                    }
                }
            }
    }
}*/

package ru.dvfu.appliances.compose.home.booking_list


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.dvfu.appliances.R
import ru.dvfu.appliances.compose.viewmodels.BookingListViewModel
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.utils.toMillis
import java.time.LocalDateTime

@Composable
fun PendingBookingsList(
    bookings: List<CalendarEvent>,
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
                    PendingBookingItemView(
                        booking = it[index],
                        onApproveClick = {
                            viewModel.manageBookStatus(
                                event = it[index],
                                status = BookingStatus.APPROVED
                            )
                        },
                        onDeclineClick = {
                            viewModel.manageBookStatus(
                                event = it[index],
                                status = BookingStatus.DECLINED
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MyBookingRequestsList(
    bookings: List<CalendarEvent>,
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
                    MyBookingRequestItemView(
                        booking = it[index],
                        onDeclineClick = {
                            viewModel.manageBookStatus(
                                event = it[index],
                                status = BookingStatus.DECLINED,
                                userCommentary = "Отклонено пользователем"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ApprovedBookingsList(
    bookings: List<CalendarEvent>,
    viewModel: BookingListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings.let {
                if (it.isEmpty()) {
                    item { NoBookingsView() }
                } else {
                    items(count = bookings.size) { index ->
                        BookingApprovedItemView(
                            booking = bookings[index],
                            onDeclineClick = {
                                viewModel.manageBookStatus(
                                    event = it[index],
                                    status = BookingStatus.NONE,
                                    userCommentary = "Отклонено пользователем"
                                )
                            }
                        )
                    }
                }
            }
    }
}

@Composable
fun DeclinedBookingsList(
    bookings: List<CalendarEvent>,
    viewModel: BookingListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings
            .let {
                if (it.isEmpty()) {
                    item { NoBookingsView() }
                } else {
                    items(count = bookings.size) { index ->
                        BookingDeclinedOrPastItemView(
                            booking = bookings[index]
                        )
                    }
                }
            }
    }
}

@Composable
fun PastBookingsList(
    bookings: List<CalendarEvent>,
    viewModel: BookingListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings
            .sortedByDescending { it.date }
            .let {
                if (it.isEmpty()) {
                    item { NoBookingsView() }
                } else {
                    items(count = bookings.size) { index ->
                        BookingDeclinedOrPastItemView(
                            booking = bookings[index]
                        )
                    }
                }
            }
    }
}

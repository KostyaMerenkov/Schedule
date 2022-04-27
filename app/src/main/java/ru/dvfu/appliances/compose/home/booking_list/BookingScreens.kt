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
import ru.dvfu.appliances.compose.viewmodels.BookingListViewModel
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import ru.dvfu.appliances.model.utils.toMillis
import java.time.LocalDateTime
import java.util.*

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
                    BookingRequestItemView(
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
fun ApprovedBookingsList(
    bookings: List<CalendarEvent>,
    viewModel: BookingListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings.filter { it.status == BookingStatus.APPROVED }
            .let {
                if (it.isEmpty()) {
                    item { NoBookingsView() }
                } else {
                    items(count = bookings.size) { index ->
                        BookingApprovedItemView(
                            booking = bookings[index],
                            onDeclineClick = {
                                viewModel.manageBookStatus(
                                    event = it[index],
                                    status = BookingStatus.NONE
                                )
                            }
                        )
                    }
                }
            }
    }
}

@Composable
fun DeclinedAndPastBookingsList(
    bookings: List<CalendarEvent>,
    viewModel: BookingListViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings.filter {
            it.status == BookingStatus.DECLINED ||
                    ((it.status == BookingStatus.APPROVED)
                            && it.timeEnd.toMillis > LocalDateTime.now().toMillis)
        }
            .let {
                if (it.isEmpty()) {
                    item { NoBookingsView() }
                } else {
                    items(count = bookings.size) { index ->
                        BookingDeclinedItemView(
                            booking = bookings[index]
                        )
                    }
                }
            }
    }
}

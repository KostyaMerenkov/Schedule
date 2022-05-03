package ru.dvfu.appliances.compose.home.booking_list


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ru.dvfu.appliances.compose.viewmodels.BookingListViewModel
import ru.dvfu.appliances.model.repository.entity.BookingStatus
import ru.dvfu.appliances.model.repository.entity.CalendarEvent
import java.time.LocalDateTime

@Composable
fun PendingBookingsList(
    bookings: List<CalendarEvent>,
    viewModel: BookingListViewModel,
    navController: NavController
) {
    val currentUser by viewModel.currentUser.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings.filter { it.status == BookingStatus.NONE }.let { events ->
            if (events.isEmpty()) {
                item { NoBookingsView() }
            } else {
                items(count = events.size) { index ->
                    PendingBookingItemView(
                        currentUser = currentUser,
                        booking = events[index],
                        navController = navController,
                        onApproveClick = { event, comment ->
                            viewModel.manageBookStatus(
                                event = event,
                                managerCommentary = comment,
                                status = BookingStatus.APPROVED
                            )
                        },
                        onDeclineClick = { event, comment ->
                            viewModel.manageBookStatus(
                                event = event,
                                managerCommentary = comment,
                                status = BookingStatus.DECLINED
                            )
                        },
                        onSetDateAndTime = {
                            viewModel.updateEventDateAndTime(
                                event = events[index],
                                eventDateAndTime = it
                            )
                        },
                        onApplyCommentary = { event, userComment ->
                            // TODO: change user comment
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MyBookingsList(
    bookings: List<CalendarEvent>,
    viewModel: BookingListViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings.let { events ->
            if (events.isEmpty()) {
                item { NoBookingsView() }
            } else {
                items(count = events.size) { index ->
                    MyBookingRequestItemView(
                        booking = events[index],
                        onDeclineClick = {
                            viewModel.manageBookStatus(
                                event = events[index],
                                status = BookingStatus.DECLINED,
                                userCommentary = "Отклонено пользователем"
                            )
                        },
                        onSetDateAndTime = {
                            viewModel.updateEventDateAndTime(
                                event = events[index],
                                eventDateAndTime = it
                            )
                        },
                        onApplyCommentary = { _, _ ->
                            // TODO:
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PastBookingsList(
    bookings: List<CalendarEvent>,
    viewModel: BookingListViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        bookings.sortedByDescending { it.date }
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

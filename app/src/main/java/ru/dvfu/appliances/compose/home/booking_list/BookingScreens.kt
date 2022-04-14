package ru.dvfu.appliances.compose.home.booking_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.dvfu.appliances.model.repository.entity.UiBooking

@Composable
fun PendingBookingsList(bookings: List<UiBooking>) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(modifier = Modifier.wrapContentSize(), text = "Pending")
    }
}

@Composable
fun ApprovedBookingsList(bookings: List<UiBooking>) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(modifier = Modifier.wrapContentSize(), text = "Approved")
    }
}

@Composable
fun DeclinedAndPastBookingsList(bookings: List<UiBooking>) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(modifier = Modifier.wrapContentSize(), text = "Declined")
    }

}
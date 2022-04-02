package ru.dvfu.appliances.compose.utils

sealed class AvailabilityState() {
    object Available : AvailabilityState()
    object NotAvailable : AvailabilityState()
    object Error : AvailabilityState()
}

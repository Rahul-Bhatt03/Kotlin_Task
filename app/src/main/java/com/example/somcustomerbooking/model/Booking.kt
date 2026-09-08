package com.example.somcustomerbooking.model

data class Booking(
    val id: String,
    val serviceId: String,
    val slotId: String,
    val customerName: String,
    val customerEmail: String,
    val status: BookingStatus = BookingStatus.PENDING
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

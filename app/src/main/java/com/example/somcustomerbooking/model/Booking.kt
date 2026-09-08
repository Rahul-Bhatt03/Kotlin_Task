package com.example.somcustomerbooking.model

data class Booking(
    val id: String,
    val bookingNumber: String,
    val serviceId: String,
    val serviceName:String,
    val provider: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val customerName: String,
    val contactInfo: String,
    val customerEmail: String,
    val status: BookingStatus = BookingStatus.PENDING
)

//Input required to create a booking.
data class BookingRequest(
    val serviceId: String,
    val slotId: String,
    val date: String,
    val time: String,
    val customerName: String,
    val contactInfo: String
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

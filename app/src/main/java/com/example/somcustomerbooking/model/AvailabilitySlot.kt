package com.example.somcustomerbooking.model

data class AvailabilitySlot(
    val id: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val available: Boolean = true
)

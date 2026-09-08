package com.example.somcustomerbooking.model

import java.time.LocalDateTime

data class AvailabilitySlot(
    val id: String,
    val serviceId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isAvailable: Boolean = true
)

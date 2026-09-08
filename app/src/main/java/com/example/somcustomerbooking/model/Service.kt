package com.example.somcustomerbooking.model

data class Service(
    val id: String,
    val name: String,
    val category: String,
    val provider: String,
    val description: String,
    val price: Double,
    val currency: String,
    val durationMinutes: Int,
    val rating: Double
)

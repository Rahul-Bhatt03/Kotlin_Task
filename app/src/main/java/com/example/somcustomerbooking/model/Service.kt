package com.example.somcustomerbooking.model

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String? = null
)

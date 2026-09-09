package com.example.somcustomerbooking.data

import com.example.somcustomerbooking.data.mock.MockApiService
import com.example.somcustomerbooking.data.repository.BookingRepository
import com.example.somcustomerbooking.data.repository.BookingRepositoryImpl

object AppContainer {
    private val apiService = MockApiService()

    val bookingRepository: BookingRepository by lazy {
        BookingRepositoryImpl(apiService)
    }
}

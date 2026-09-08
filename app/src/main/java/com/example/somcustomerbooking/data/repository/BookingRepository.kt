package com.example.somcustomerbooking.data.repository

import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.AvailabilitySlot
import com.example.somcustomerbooking.model.Booking
import com.example.somcustomerbooking.model.BookingRequest
import com.example.somcustomerbooking.model.Service

/**
 * Application-facing data boundary. ViewModels depend on this interface,
 * never on [com.example.sombooking.data.api.ApiService] or the mock layer
 * directly -- that keeps the presentation layer ignorant of whether data is
 * coming from the mock implementation or (later) a real HTTP client.
 */
interface BookingRepository {
    suspend fun getServices(query: String? = null): ApiResult<List<Service>>
    suspend fun getServiceDetails(serviceId: String): ApiResult<Service>
    suspend fun getAvailability(serviceId: String, date: String): ApiResult<List<AvailabilitySlot>>
    suspend fun createBooking(request: BookingRequest): ApiResult<Booking>
    suspend fun getBookings(): ApiResult<List<Booking>>
}

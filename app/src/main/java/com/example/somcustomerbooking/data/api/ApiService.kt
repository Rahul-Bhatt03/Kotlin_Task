package com.example.somcustomerbooking.data.api

import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.AvailabilitySlot
import com.example.somcustomerbooking.model.Booking
import com.example.somcustomerbooking.model.BookingRequest
import com.example.somcustomerbooking.model.Service

interface ApiService {
    suspend fun getServices(query: String? = null): ApiResult<List<Service>>
    suspend fun getServiceById(serviceId: String): ApiResult<Service>
    suspend fun getAvailability(serviceId: String, date: String): ApiResult<List<AvailabilitySlot>>
    suspend fun createBooking(request: BookingRequest): ApiResult<Booking>
    suspend fun getBookings(): ApiResult<List<Booking>>
}

package com.example.somcustomerbooking.data.repository

import com.example.somcustomerbooking.data.api.ApiService
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.AvailabilitySlot
import com.example.somcustomerbooking.model.Booking
import com.example.somcustomerbooking.model.BookingRequest
import com.example.somcustomerbooking.model.Service

/**
 * Default [BookingRepository] backed by an [ApiService]. Today that is
 * [com.example.sombooking.data.mock.MockApiService]; a real deployment would
 * inject a Retrofit-based ApiService implementation here instead -- this
 * class, and everything above it, would not need to change.
 */
class BookingRepositoryImpl(
    private val apiService: ApiService
) : BookingRepository {

    override suspend fun getServices(query: String?): ApiResult<List<Service>> =
        apiService.getServices(query)

    override suspend fun getServiceDetails(serviceId: String): ApiResult<Service> =
        apiService.getServiceById(serviceId)

    override suspend fun getAvailability(serviceId: String, date: String): ApiResult<List<AvailabilitySlot>> =
        apiService.getAvailability(serviceId, date)

    override suspend fun createBooking(request: BookingRequest): ApiResult<Booking> =
        apiService.createBooking(request)

    override suspend fun getBookings(): ApiResult<List<Booking>> =
        apiService.getBookings()
}

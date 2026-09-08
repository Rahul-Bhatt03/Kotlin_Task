package com.example.somcustomerbooking.fake

import com.example.somcustomerbooking.data.repository.BookingRepository
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.AvailabilitySlot
import com.example.somcustomerbooking.model.Booking
import com.example.somcustomerbooking.model.BookingRequest
import com.example.somcustomerbooking.model.BookingStatus
import com.example.somcustomerbooking.model.Service

/**
 * Controllable test double for [BookingRepository]. Each response is set
 * via a mutable field so a test can dictate exactly what the "network"
 * returns without depending on MockApiService's specific dataset --
 * ViewModel tests should verify state-mapping logic, not the mock dataset
 * (that is covered separately by MockApiServiceTest).
 */
class FakeBookingRepository : BookingRepository {

    var servicesResult: ApiResult<List<Service>> = ApiResult.Success(emptyList())
    var serviceDetailsResult: ApiResult<Service> = ApiResult.ServerError("not configured")
    var availabilityResult: ApiResult<List<AvailabilitySlot>> = ApiResult.Success(emptyList())
    var createBookingResult: ApiResult<Booking> = ApiResult.ServerError("not configured")
    var bookingsResult: ApiResult<List<Booking>> = ApiResult.Success(emptyList())

    override suspend fun getServices(query: String?): ApiResult<List<Service>> = servicesResult
    override suspend fun getServiceDetails(serviceId: String): ApiResult<Service> = serviceDetailsResult
    override suspend fun getAvailability(serviceId: String, date: String): ApiResult<List<AvailabilitySlot>> = availabilityResult
    override suspend fun createBooking(request: BookingRequest): ApiResult<Booking> = createBookingResult
    override suspend fun getBookings(): ApiResult<List<Booking>> = bookingsResult

    companion object {
        fun sampleService(id: String = "svc-001") = Service(
            id = id,
            name = "Deep House Cleaning",
            category = "Cleaning",
            provider = "SparkleHome Services",
            description = "A thorough clean.",
            price = 45.0,
            currency = "USD",
            durationMinutes = 120,
            rating = 4.8,
        )

        fun sampleSlot(id: String = "slot-1") = AvailabilitySlot(
            id = id,
            date = "2026-09-10",
            startTime = "09:00",
            endTime = "10:00",
            available = true,
        )

        fun sampleBooking() = Booking(
            id = "bk-1",
            bookingNumber = "SOM-1001",
            serviceId = "svc-001",
            serviceName = "Deep House Cleaning",
            provider = "SparkleHome Services",
            scheduledDate = "2026-09-10",
            scheduledTime = "09:00",
            customerName = "Jane Doe",
            contactInfo = "9800000000",
            customerEmail = "jane@example.com",
            status = BookingStatus.CONFIRMED,
        )
    }
}

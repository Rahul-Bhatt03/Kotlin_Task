package com.example.somcustomerbooking.data.mock

import com.example.somcustomerbooking.data.api.ApiService
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.AvailabilitySlot
import com.example.somcustomerbooking.model.Booking
import com.example.somcustomerbooking.model.BookingRequest
import com.example.somcustomerbooking.model.BookingStatus
import com.example.somcustomerbooking.model.Service
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger

/**
 * In-memory implementation of [ApiService]. This stands in for a real HTTP
 * client: it simulates latency with [delay] and returns the same
 * [ApiResult] shapes a Retrofit-backed implementation would produce after
 * mapping HTTP responses, so swapping this class out later does not ripple
 * into the Repository or above.
 *
 * State (created bookings) is held in memory for the lifetime of the
 * process, which is sufficient for a demo/assignment; a real implementation
 * would obviously persist server-side.
 */
class MockApiService : ApiService {

    private val bookingsMutex = Mutex()
    private val createdBookings = mutableListOf<Booking>()
    private val bookingCounter = AtomicInteger(1000)

    override suspend fun getServices(query: String?): ApiResult<List<Service>> {
        delay(NETWORK_DELAY_MS)
        val all = MockData.services
        val filtered = if (query.isNullOrBlank()) {
            all
        } else {
            all.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.provider.contains(query, ignoreCase = true)
            }
        }
        return ApiResult.Success(filtered)
    }

    override suspend fun getServiceById(serviceId: String): ApiResult<Service> {
        delay(NETWORK_DELAY_MS)
        if (serviceId == "svc-004") {
            // Deliberate demo trigger: see docs/setup.md "Demonstrating error states".
            return ApiResult.ServerError("The service provider is temporarily unavailable. Please try again later.")
        }
        val service = MockData.findService(serviceId)
            ?: return ApiResult.ServerError("Service not found.")
        return ApiResult.Success(service)
    }

    override suspend fun getAvailability(serviceId: String, date: String): ApiResult<List<AvailabilitySlot>> {
        delay(NETWORK_DELAY_MS)
        if (serviceId == "svc-004") {
            return ApiResult.ServerError("Unable to load availability right now. Please try again later.")
        }
        return ApiResult.Success(MockData.availabilityFor(serviceId, date))
    }

    override suspend fun createBooking(request: BookingRequest): ApiResult<Booking> {
        delay(NETWORK_DELAY_MS)

        // Server-side validation, independent from (and in addition to) the
        // ViewModel's client-side validation, per assignment requirements.
        val fieldErrors = mutableMapOf<String, String>()
        if (request.customerName.isBlank()) {
            fieldErrors["customerName"] = "Name is required."
        }
        if (request.contactInfo.trim().length < 6) {
            fieldErrors["contactInfo"] = "Enter a valid phone number or address."
        }
        if (fieldErrors.isNotEmpty()) {
            return ApiResult.ValidationError(fieldErrors)
        }

        if (request.slotId.endsWith("-taken") || request.slotId.endsWith("-conflict")) {
            return ApiResult.Conflict("This time slot was just booked by someone else. Please choose another slot.")
        }

        val service = MockData.findService(request.serviceId)
            ?: return ApiResult.ServerError("Service not found.")

        val booking = Booking(
            id = "bk-${bookingCounter.incrementAndGet()}",
            bookingNumber = "SOM-${bookingCounter.get()}",
            serviceId = service.id,
            serviceName = service.name,
            provider = service.provider,
            scheduledDate = request.date,
            scheduledTime = request.time,
            customerName = request.customerName,
            customerEmail = request.customerEmail,
            contactInfo = request.contactInfo,
            status = BookingStatus.CONFIRMED,
        )

        bookingsMutex.withLock { createdBookings.add(0, booking) }
        return ApiResult.Success(booking)
    }

    override suspend fun getBookings(): ApiResult<List<Booking>> {
        delay(NETWORK_DELAY_MS)
        val snapshot = bookingsMutex.withLock { createdBookings.toList() }
        return ApiResult.Success(snapshot)
    }

    private companion object {
        const val NETWORK_DELAY_MS = 600L
    }
}

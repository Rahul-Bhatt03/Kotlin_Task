package com.example.somcustomerbooking.data

import com.example.somcustomerbooking.data.mock.MockApiService
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.BookingRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Covers the mock data-layer directly: these are the deterministic demo
 * triggers documented in MockData.kt and docs/setup.md, so a test here
 * doubles as a regression guard for the demo itself.
 */
class MockApiServiceTest {

    private lateinit var api: MockApiService

    @Before
    fun setUp() {
        api = MockApiService()
    }

    @Test
    fun `getServices returns full list when query is blank`() = runTest {
        val result = api.getServices(null)
        assertTrue(result is ApiResult.Success)
        assertEquals(5, (result as ApiResult.Success).data.size)
    }

    @Test
    fun `getServices returns empty list for a query with no matches`() = runTest {
        val result = api.getServices("zzz-no-match")
        assertTrue(result is ApiResult.Success)
        assertTrue((result as ApiResult.Success).data.isEmpty())
    }

    @Test
    fun `getServiceById returns ServerError for the demo error service`() = runTest {
        val result = api.getServiceById("svc-004")
        assertTrue(result is ApiResult.ServerError)
    }

    @Test
    fun `getAvailability returns empty for the demo empty-availability service`() = runTest {
        val result = api.getAvailability("svc-005", "2026-09-10")
        assertTrue(result is ApiResult.Success)
        assertTrue((result as ApiResult.Success).data.isEmpty())
    }

    @Test
    fun `createBooking returns ValidationError when name is blank`() = runTest {
        val request = BookingRequest(
            serviceId = "svc-001",
            slotId = "svc-001-2026-09-10-slot1",
            date = "2026-09-10",
            time = "11:00",
            customerName = "",
            customerEmail = "jane@example.com",
            contactInfo = "9800000000",
        )
        val result = api.createBooking(request)
        assertTrue(result is ApiResult.ValidationError)
        assertTrue((result as ApiResult.ValidationError).fieldErrors.containsKey("customerName"))
    }

    @Test
    fun `createBooking returns Conflict for a slot that was just taken by someone else`() = runTest {
        val request = BookingRequest(
            serviceId = "svc-003",
            slotId = "svc-003-2026-09-10-slot1-conflict",
            date = "2026-09-10",
            time = "11:00",
            customerName = "Jane Doe",
            customerEmail = "jane@example.com",
            contactInfo = "9800000000",
        )
        val result = api.createBooking(request)
        assertTrue(result is ApiResult.Conflict)
    }

    @Test
    fun `getAvailability marks the known pre-booked slot as unavailable`() = runTest {
        val result = api.getAvailability("svc-002", "2026-09-10")
        assertTrue(result is ApiResult.Success)
        val firstSlot = (result as ApiResult.Success).data.first()
        assertTrue(!firstSlot.available)
    }

    @Test
    fun `createBooking succeeds for a valid request and appears in getBookings`() = runTest {
        val request = BookingRequest(
            serviceId = "svc-001",
            slotId = "svc-001-2026-09-10-slot1",
            date = "2026-09-10",
            time = "11:00",
            customerName = "Jane Doe",
            customerEmail = "jane@example.com",
            contactInfo = "9800000000",
        )
        val createResult = api.createBooking(request)
        assertTrue(createResult is ApiResult.Success)

        val listResult = api.getBookings()
        assertTrue(listResult is ApiResult.Success)
        assertEquals(1, (listResult as ApiResult.Success).data.size)
        assertEquals("svc-001", listResult.data.first().serviceId)
    }
}

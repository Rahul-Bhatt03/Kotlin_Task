package com.example.somcustomerbooking.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Centralised route definitions. Only IDs are ever passed between screens
 * (serviceId, slotId, date, bookingId) rather than whole objects -- each
 * destination re-fetches what it needs from the Repository, which keeps
 * navigation arguments simple, survives process death better than a
 * SavedStateHandle full of parcelled objects, and matches how a real app
 * backed by deep links would work.
 */
object Screen {
    const val SERVICE_LIST = "service_list"

    const val SERVICE_DETAILS_ROUTE = "service_details/{serviceId}"
    fun serviceDetails(serviceId: String) = "service_details/${encode(serviceId)}"

    const val BOOKING_ROUTE = "booking/{serviceId}/{date}/{slotId}"
    fun booking(serviceId: String, date: String, slotId: String) =
        "booking/${encode(serviceId)}/${encode(date)}/${encode(slotId)}"

    // There is deliberately no separate "booking_confirmation" route: the
    // confirmation is rendered as a state within the Booking screen once
    // submission succeeds (see docs/decisions.md, "Booking confirmation as
    // a screen state"). This avoids passing a full Booking object through
    // navigation args just to display a one-off success view.
    const val MY_BOOKINGS = "my_bookings"

    private fun encode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
}

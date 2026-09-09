package com.example.somcustomerbooking.data.mock

import com.example.somcustomerbooking.model.AvailabilitySlot
import com.example.somcustomerbooking.model.Service

/**
 * Deterministic demo triggers:
 *  - Service "svc-004" (Home Legal Consultation) -> getServiceById /
 *    getAvailability return ApiResult.ServerError, simulating a provider
 *    whose backend is temporarily down.
 *  - Service "svc-005" (Piano Tuning) -> getAvailability on any date returns
 *    an empty list (provider has no open slots), demonstrating the Empty
 *    state on the Service Details screen.
 *  - Searching services for "zzz" (or anything with no matches) -> empty
 *    Service List.
 *  - The first slot of svc-002 ("AC Repair & Maintenance") is always shown
 *    already booked (disabled, id ending "-taken") -- demonstrates a slot
 *    known unavailable up front.
 *  - The second slot of svc-003 ("Personal Yoga Session") looks available
 *    and is selectable, but attempting to book it (id ending "-conflict")
 *    returns ApiResult.Conflict (HTTP 409 equivalent) -- demonstrates a
 *    slot that was taken by someone else between viewing and confirming,
 *    which is reachable through the normal UI flow.
 *  - Submitting a booking with a blank name or a contact value under 6
 *    characters -> ApiResult.ValidationError from the mock layer itself,
 *    demonstrating server-side validation independent of client-side checks.
 */
object MockData {

    val services: List<Service> = listOf(
        Service(
            id = "svc-001",
            name = "Deep House Cleaning",
            category = "Cleaning",
            provider = "SparkleHome Services",
            description = "A thorough top-to-bottom clean covering kitchen, bathrooms, " +
                    "living areas and bedrooms. Includes eco-friendly supplies.",
            price = 45.0,
            currency = "USD",
            durationMinutes = 120,
            rating = 4.8,
        ),
        Service(
            id = "svc-002",
            name = "AC Repair & Maintenance",
            category = "Home Repair",
            provider = "CoolFix Technicians",
            description = "Diagnosis and repair of residential air conditioning units, " +
                    "including gas top-up and filter replacement.",
            price = 60.0,
            currency = "USD",
            durationMinutes = 90,
            rating = 4.6,
        ),
        Service(
            id = "svc-003",
            name = "Personal Yoga Session",
            category = "Wellness",
            provider = "Mindful Movement Studio",
            description = "One-on-one yoga session tailored to your fitness level, " +
            "held at your home or a nearby studio.",
            price = 35.0,
            currency = "USD",
            durationMinutes = 60,
            rating = 4.9,
        ),
        Service(
            id = "svc-004",
            name = "Home Legal Consultation",
            category = "Consulting",
            provider = "Ashford & Partners",
            description = "A general consultation covering tenancy, contracts or " +
            "small-claims questions.",
            price = 80.0,
            currency = "USD",
            durationMinutes = 45,
            rating = 4.5,
        ),
        Service(
            id = "svc-005",
            name = "Piano Tuning",
            category = "Home Repair",
            provider = "Harmony Piano Care",
            description = "Professional tuning and minor action adjustment for upright " +
            "and grand pianos.",
            price = 55.0,
            currency = "USD",
            durationMinutes = 75,
            rating = 4.7,
        ),
    )

    fun findService(serviceId: String): Service? = services.find { it.id == serviceId }

    /**
     * Availability is generated per (serviceId, date) rather than stored as
     * a giant static table, so any date the user picks in the UI's date
     * selector "just works" -- except for the deliberate demo triggers
     * above, which are checked first.
     */
    fun availabilityFor(serviceId: String, date: String): List<AvailabilitySlot> {
        if (serviceId == "svc-005") return emptyList()

        val baseSlots = listOf("09:00" to "10:00", "11:00" to "12:00", "14:00" to "15:00", "16:00" to "17:00")
        return baseSlots.mapIndexed { index, (start, end) ->
            when {
                // The first slot of svc-002 is shown as already booked
                // (disabled in the UI) -- demonstrates a slot that is known
                // unavailable up front.
                (serviceId == "svc-002" && index == 0) -> AvailabilitySlot(
                    id = "$serviceId-$date-slot$index-taken",
                    date = date,
                    startTime = start,
                    endTime = end,
                    available = false,
                )
                // The second slot of svc-003 looks perfectly available and
                // selectable in the UI, but booking it simulates another
                // customer grabbing it a moment earlier -- this is the
                // reliable, UI-reachable way to demo a 409 conflict
                // response (see MockApiService.createBooking).
                (serviceId == "svc-003" && index == 1) -> AvailabilitySlot(
                    id = "$serviceId-$date-slot$index-conflict",
                    date = date,
                    startTime = start,
                    endTime = end,
                    available = true,
                )
                else -> AvailabilitySlot(
                    id = "$serviceId-$date-slot$index",
                    date = date,
                    startTime = start,
                    endTime = end,
                    available = true,
                )
            }
        }
    }
}

package com.example.somcustomerbooking.viewmodel

import com.example.somcustomerbooking.fake.FakeBookingRepository
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BookingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildViewModel(repository: FakeBookingRepository): BookingViewModel {
        repository.serviceDetailsResult = ApiResult.Success(FakeBookingRepository.sampleService())
        repository.availabilityResult = ApiResult.Success(listOf(FakeBookingRepository.sampleSlot(id = "slot-1")))
        return BookingViewModel(
            serviceId = "svc-001",
            slotId = "slot-1",
            date = "2026-09-10",
            repository = repository,
        )
    }

    @Test
    fun `submit with blank name sets a field validation error and does not call repository`() = runTest {
        val repository = FakeBookingRepository()
        val viewModel = buildViewModel(repository)
        advanceUntilIdle()

        viewModel.onNameChanged("")
        viewModel.onEmailChanged("jane@example.com")
        viewModel.onContactChanged("9800000000")
        viewModel.submit()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.fieldErrors.containsKey("customerName"))
        assertTrue(viewModel.uiState.value.submission is BookingSubmissionState.Idle)
    }

    @Test
    fun `successful submit moves to Success state`() = runTest {
        val repository = FakeBookingRepository()
        repository.createBookingResult = ApiResult.Success(FakeBookingRepository.sampleBooking())
        val viewModel = buildViewModel(repository)
        advanceUntilIdle()

        viewModel.onNameChanged("Jane Doe")
        viewModel.onEmailChanged("jane@example.com")
        viewModel.onContactChanged("9800000000")
        viewModel.submit()
        advanceUntilIdle()

        val submission = viewModel.uiState.value.submission
        assertTrue(submission is BookingSubmissionState.Success)
        assertEquals("SOM-1001", (submission as BookingSubmissionState.Success).booking.bookingNumber)
    }

    @Test
    fun `slot conflict maps to Conflict submission state`() = runTest {
        val repository = FakeBookingRepository()
        repository.createBookingResult = ApiResult.Conflict("Slot taken")
        val viewModel = buildViewModel(repository)
        advanceUntilIdle()

        viewModel.onNameChanged("Jane Doe")
        viewModel.onEmailChanged("jane@example.com")
        viewModel.onContactChanged("9800000000")
        viewModel.submit()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.submission is BookingSubmissionState.Conflict)
    }
}

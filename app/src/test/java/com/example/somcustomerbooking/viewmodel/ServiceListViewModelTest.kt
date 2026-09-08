package com.example.somcustomerbooking.viewmodel

import com.example.somcustomerbooking.fake.FakeBookingRepository
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.UiState
import com.example.somcustomerbooking.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ServiceListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads services successfully on init`() = runTest {
        val repository = FakeBookingRepository().apply {
            servicesResult = ApiResult.Success(listOf(FakeBookingRepository.sampleService()))
        }
        val viewModel = ServiceListViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value.listState
        assertTrue(state is UiState.Success)
        assertEquals(1, (state as UiState.Success<*>).data.let { (it as List<*>).size })
    }

    @Test
    fun `empty service list maps to Empty state, not Error`() = runTest {
        val repository = FakeBookingRepository().apply {
            servicesResult = ApiResult.Success(emptyList())
        }
        val viewModel = ServiceListViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.listState is UiState.Empty)
    }

    @Test
    fun `server error maps to Error state with message`() = runTest {
        val repository = FakeBookingRepository().apply {
            servicesResult = ApiResult.ServerError("Service unavailable")
        }
        val viewModel = ServiceListViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.uiState.value.listState
        assertTrue(state is UiState.Error)
        assertEquals("Service unavailable", (state as UiState.Error).message)
    }
}

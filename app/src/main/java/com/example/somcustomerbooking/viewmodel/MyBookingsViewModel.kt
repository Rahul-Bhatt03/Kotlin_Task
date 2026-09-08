package com.example.somcustomerbooking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somcustomerbooking.data.repository.BookingRepository
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.Booking
import com.example.somcustomerbooking.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class MyBookingsViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Booking>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Booking>>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    fun load() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }
            when (val result = repository.getBookings()) {
                is ApiResult.Success -> _uiState.update {
                    if (result.data.isEmpty()) UiState.Empty else UiState.Success(result.data)
                }
                is ApiResult.ServerError -> _uiState.update { UiState.Error(result.message) }
                is ApiResult.Conflict -> _uiState.update { UiState.Error(result.message) }
                is ApiResult.ValidationError -> _uiState.update { UiState.Error("Unable to load your bookings.") }
            }
        }
    }
}


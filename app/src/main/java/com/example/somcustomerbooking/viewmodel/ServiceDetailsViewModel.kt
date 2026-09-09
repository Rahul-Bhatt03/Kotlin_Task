package com.example.somcustomerbooking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somcustomerbooking.data.repository.BookingRepository
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.AvailabilitySlot
import com.example.somcustomerbooking.model.Service
import com.example.somcustomerbooking.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ServiceDetailsUiState(
    val serviceState: UiState<Service> = UiState.Loading,
    val selectedDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val availabilityState: UiState<List<AvailabilitySlot>> = UiState.Loading,
    val selectedSlot: AvailabilitySlot? = null
)

class ServiceDetailsViewModel(
    private val serviceId: String,
    private val repository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceDetailsUiState())
    val uiState: StateFlow<ServiceDetailsUiState> = _uiState.asStateFlow()

    init {
        loadService()
        loadAvailability()
    }

    fun onDateSelected(date: String) {
        // Changing the date invalidates any previously selected slot -- a
        // slot ID is only meaningful for the date it was generated for.
        _uiState.update { it.copy(selectedDate = date, selectedSlot = null) }
        loadAvailability()
    }

    fun onSlotSelected(slot: AvailabilitySlot) {
        if (!slot.available) return
        _uiState.update { it.copy(selectedSlot = slot) }
    }

    fun retryService() = loadService()
    fun retryAvailability() = loadAvailability()

    private fun loadService() {
        viewModelScope.launch {
            _uiState.update { it.copy(serviceState = UiState.Loading) }
            when (val result = repository.getServiceDetails(serviceId)) {
                is ApiResult.Success -> _uiState.update { it.copy(serviceState = UiState.Success(result.data)) }
                is ApiResult.ServerError -> _uiState.update { it.copy(serviceState = UiState.Error(result.message)) }
                is ApiResult.Conflict -> _uiState.update { it.copy(serviceState = UiState.Error(result.message)) }
                is ApiResult.ValidationError -> _uiState.update {
                    it.copy(serviceState = UiState.Error("Unable to load this service."))
                }
            }
        }
    }

    private fun loadAvailability() {
        viewModelScope.launch {
            _uiState.update { it.copy(availabilityState = UiState.Loading) }
            when (val result = repository.getAvailability(serviceId, _uiState.value.selectedDate)) {
                is ApiResult.Success -> {
                    val slots = result.data
                    _uiState.update {
                        it.copy(
                            availabilityState = if (slots.isEmpty()) UiState.Empty else UiState.Success(slots)
                        )
                    }
                }
                is ApiResult.ServerError -> _uiState.update { it.copy(availabilityState = UiState.Error(result.message)) }
                is ApiResult.Conflict -> _uiState.update { it.copy(availabilityState = UiState.Error(result.message)) }
                is ApiResult.ValidationError -> _uiState.update {
                    it.copy(availabilityState = UiState.Error("Unable to load availability."))
                }
            }
        }
    }
}

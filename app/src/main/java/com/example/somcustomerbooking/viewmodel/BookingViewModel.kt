package com.example.somcustomerbooking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.AvailabilitySlot
import com.example.somcustomerbooking.model.Booking
import com.example.somcustomerbooking.model.BookingRequest
import com.example.somcustomerbooking.model.Service
import com.example.somcustomerbooking.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//Represents the outcome of submitting the booking form, separate from
//* [UiState] because the vocabulary here is domain-specific (a booking can
//* conflict, which is not a generic "error").

sealed interface BookingSubmissionState {
    data object Idle : BookingSubmissionState
    data object Submitting : BookingSubmissionState
    data class Success(val booking: Booking) : BookingSubmissionState
    data class Conflict(val message: String) : BookingSubmissionState
    data class Error(val message: String) : BookingSubmissionState
}

data class BookingUiState(
    val serviceState: UiState<Service> = UiState.Loading,
    val slot: AvailabilitySlot? = null,
    val date: String = "",
    val customerName: String = "",
    val contactInfo: String = "",
    val fieldErrors: Map<String, String> = emptyMap(),
    val submission: BookingSubmissionState = BookingSubmissionState.Idle
)

class BookingViewModel(
    private val serviceId: String,
    private val slotId: String,
    private val date: String,
    private val repository: BookingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookingUiState(date = date))
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        loadSummary()
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(customerName = value, fieldErrors = it.fieldErrors - "customerName") }
    }

    fun onContactChanged(value: String) {
        _uiState.update { it.copy(contactInfo = value, fieldErrors = it.fieldErrors - "contactInfo") }
    }

    /**
     * Submits the booking. Guards against duplicate submissions by ignoring
     * the call outright while a request is already in flight -- the UI also
     * disables the confirm button while Submitting, this is defense in
     * depth in case of e.g. a double-tap racing the recomposition.
     */
    fun submit() {
        if (_uiState.value.submission is BookingSubmissionState.Submitting) return

        val errors = validate(_uiState.value.customerName, _uiState.value.contactInfo)
        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(fieldErrors = errors) }
            return
        }

        val slot = _uiState.value.slot ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(submission = BookingSubmissionState.Submitting) }
            val request = BookingRequest(
                serviceId = serviceId,
                slotId = slot.id,
                date = date,
                time = slot.startTime,
                customerName = _uiState.value.customerName.trim(),
                contactInfo = _uiState.value.contactInfo.trim()
            )
            when (val result = repository.createBooking(request)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(submission = BookingSubmissionState.Success(result.data))
                }
                is ApiResult.ValidationError -> _uiState.update {
                    it.copy(fieldErrors = result.fieldErrors, submission = BookingSubmissionState.Idle)
                }
                is ApiResult.Conflict -> _uiState.update {
                    it.copy(submission = BookingSubmissionState.Conflict(result.message))
                }
                is ApiResult.ServerError -> _uiState.update {
                    it.copy(submission = BookingSubmissionState.Error(result.message))
                }
            }
        }
    }

    fun dismissSubmissionError() {
        _uiState.update { it.copy(submission = BookingSubmissionState.Idle) }
    }

    private fun validate(name: String, contact: String): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (name.isBlank()) errors["customerName"] = "Name is required."
        if (contact.trim().length < 6) errors["contactInfo"] = "Enter a valid phone number or address."
        return errors
    }

    private fun loadSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(serviceState = UiState.Loading) }
            when (val serviceResult = repository.getServiceDetails(serviceId)) {
                is ApiResult.Success -> _uiState.update { it.copy(serviceState = UiState.Success(serviceResult.data)) }
                else -> _uiState.update { it.copy(serviceState = UiState.Error("Unable to load booking summary.")) }
            }

            when (val availabilityResult = repository.getAvailability(serviceId, date)) {
                is ApiResult.Success -> {
                    val slot = availabilityResult.data.find { it.id == slotId }
                    _uiState.update { it.copy(slot = slot) }
                }
                else -> Unit // Slot summary is best-effort; the confirm button stays disabled without a slot.
            }
        }
    }
}

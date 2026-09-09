package com.example.somcustomerbooking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.somcustomerbooking.data.repository.BookingRepository
import com.example.somcustomerbooking.model.ApiResult
import com.example.somcustomerbooking.model.Service
import com.example.somcustomerbooking.model.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServiceListUiState(
    val query: String = "",
    val listState: UiState<List<Service>> = UiState.Loading
)

class ServiceListViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceListUiState())
    val uiState: StateFlow<ServiceListUiState> = _uiState.asStateFlow()

    // Debounces rapid search input so every keystroke does not trigger a
    // separate mock "network" call -- mirrors how a real search-as-you-type
    // integration would be throttled against a real backend.
    private var searchJob: Job? = null

    init {
        loadServices()
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(SEARCH_DEBOUNCE_MS)
            loadServices()
        }
    }

    fun retry() = loadServices()

    private fun loadServices() {
        viewModelScope.launch {
            _uiState.update { it.copy(listState = UiState.Loading) }
            when (val result = repository.getServices(_uiState.value.query)) {
                is ApiResult.Success -> {
                    val services = result.data
                    _uiState.update {
                        it.copy(
                            listState = if (services.isEmpty()) UiState.Empty else UiState.Success(services)
                        )
                    }
                }
                is ApiResult.ServerError -> _uiState.update { it.copy(listState = UiState.Error(result.message)) }
                is ApiResult.Conflict -> _uiState.update { it.copy(listState = UiState.Error(result.message)) }
                is ApiResult.ValidationError -> _uiState.update {
                    it.copy(listState = UiState.Error("Unable to load services."))
                }
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}

package com.example.somcustomerbooking.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.somcustomerbooking.model.AvailabilitySlot
import com.example.somcustomerbooking.model.Service
import com.example.somcustomerbooking.model.UiState
import com.example.somcustomerbooking.ui.components.EmptyView
import com.example.somcustomerbooking.ui.components.ErrorView
import com.example.somcustomerbooking.ui.components.LoadingView
import com.example.somcustomerbooking.viewmodel.ServiceDetailsViewModel
import com.example.somcustomerbooking.viewmodel.ViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailsScreen(
    serviceId: String,
    onContinueToBooking: (date: String, slotId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: ServiceDetailsViewModel = viewModel(factory = ViewModelFactory(serviceId = serviceId))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (val state = uiState.serviceState) {
                is UiState.Loading -> LoadingView(modifier = Modifier.fillMaxWidth())
                is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::retryService, modifier = Modifier.fillMaxWidth())
                is UiState.Empty -> EmptyView("Service not found.", modifier = Modifier.fillMaxWidth())
                is UiState.Success -> ServiceInfo(state.data)
            }

            Text(
                text = "Select a date",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            DatePicker(selectedDate = uiState.selectedDate, onDateSelected = viewModel::onDateSelected)

            Text(
                text = "Available time slots",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            when (val state = uiState.availabilityState) {
                is UiState.Loading -> LoadingView(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
                is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::retryAvailability, modifier = Modifier.fillMaxWidth())
                is UiState.Empty -> EmptyView(
                    "No time slots available for this date. Try another date.",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
                is UiState.Success -> SlotGrid(
                    slots = state.data,
                    selectedSlot = uiState.selectedSlot,
                    onSlotSelected = viewModel::onSlotSelected
                )
            }

            Button(
                onClick = {
                    val slot = uiState.selectedSlot
                    if (slot != null) onContinueToBooking(uiState.selectedDate, slot.id)
                },
                enabled = uiState.selectedSlot != null,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text("Continue to Booking")
            }
        }
    }
}

@Composable
private fun ServiceInfo(service: Service) {
    Column {
        Text(text = service.name, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "${service.category} • ${service.provider}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(text = service.description, modifier = Modifier.padding(top = 12.dp))

        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow("Price", "${service.currency} ${service.price}")
                InfoRow("Duration", "${service.durationMinutes} min")
                InfoRow("Rating", "${service.rating} / 5")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * A simple 7-day picker (today + next 6 days) rather than a full calendar
 * dialog -- sufficient for this assignment's scope and avoids pulling in an
 * extra date-picker dependency.
 */
@Composable
private fun DatePicker(selectedDate: String, onDateSelected: (String) -> Unit) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val displayFormatter = DateTimeFormatter.ofPattern("EEE d")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(7) { offset ->
            val date = today.plusDays(offset.toLong())
            val iso = date.format(formatter)
            FilterChip(
                selected = iso == selectedDate,
                onClick = { onDateSelected(iso) },
                label = { Text(date.format(displayFormatter)) }
            )
        }
    }
}

@Composable
private fun SlotGrid(
    slots: List<AvailabilitySlot>,
    selectedSlot: AvailabilitySlot?,
    onSlotSelected: (AvailabilitySlot) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        slots.chunked(2).forEach { rowSlots ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowSlots.forEach { slot ->
                    FilterChip(
                        selected = selectedSlot?.id == slot.id,
                        onClick = { onSlotSelected(slot) },
                        enabled = slot.available,
                        label = { Text(if (slot.available) "${slot.startTime}-${slot.endTime}" else "${slot.startTime}-${slot.endTime} (Booked)") }
                    )
                }
            }
        }
    }
}

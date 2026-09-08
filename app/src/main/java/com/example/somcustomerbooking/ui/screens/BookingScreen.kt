package com.example.somcustomerbooking.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.somcustomerbooking.model.Service
import com.example.somcustomerbooking.model.UiState
import com.example.somcustomerbooking.ui.components.ErrorView
import com.example.somcustomerbooking.viewmodel.BookingSubmissionState
import com.example.somcustomerbooking.viewmodel.BookingUiState
import com.example.somcustomerbooking.viewmodel.BookingViewModel
import com.example.somcustomerbooking.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    serviceId: String,
    date: String,
    slotId: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: BookingViewModel = viewModel(
        factory = ViewModelFactory(serviceId = serviceId, slotId = slotId, date = date)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val submission = uiState.submission

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (submission is BookingSubmissionState.Success) "Booking Confirmed" else "Confirm Booking") },
                navigationIcon = {
                    if (submission !is BookingSubmissionState.Success) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (submission is BookingSubmissionState.Success) {
            BookingConfirmation(bookingNumber = submission.booking.bookingNumber, onDone = onDone, modifier = Modifier.padding(padding))
        } else {
            BookingForm(
                uiState = uiState,
                onNameChanged = viewModel::onNameChanged,
                onContactChanged = viewModel::onContactChanged,
                onSubmit = viewModel::submit,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (submission is BookingSubmissionState.Conflict) {
        AlertDialog(
            onDismissRequest = viewModel::dismissSubmissionError,
            title = { Text("Slot no longer available") },
            text = { Text(submission.message) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissSubmissionError) { Text("OK") }
            }
        )
    }

    if (submission is BookingSubmissionState.Error) {
        AlertDialog(
            onDismissRequest = viewModel::dismissSubmissionError,
            title = { Text("Something went wrong") },
            text = { Text(submission.message) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissSubmissionError) { Text("OK") }
            }
        )
    }
}

@Composable
private fun BookingForm(
    uiState: BookingUiState,
    onNameChanged: (String) -> Unit,
    onContactChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSubmitting = uiState.submission is BookingSubmissionState.Submitting

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        when (val state = uiState.serviceState) {
            is UiState.Success -> BookingSummaryCard(state.data, uiState)
            is UiState.Error -> ErrorView(message = state.message)
            else -> Unit // Loading is brief; avoiding a second spinner keeps the form visible sooner.
        }

        Text(
            text = "Your details",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = uiState.customerName,
            onValueChange = onNameChanged,
            label = { Text("Full name") },
            singleLine = true,
            isError = uiState.fieldErrors.containsKey("customerName"),
            supportingText = uiState.fieldErrors["customerName"]?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.contactInfo,
            onValueChange = onContactChanged,
            label = { Text("Phone number or address") },
            singleLine = true,
            isError = uiState.fieldErrors.containsKey("contactInfo"),
            supportingText = uiState.fieldErrors["contactInfo"]?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Button(
            onClick = onSubmit,
            enabled = !isSubmitting && uiState.slot != null,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp).size(18.dp), strokeWidth = 2.dp)
                Text("Confirming...")
            } else {
                Text("Confirm Booking")
            }
        }
    }
}

@Composable
private fun BookingSummaryCard(service: Service, uiState: BookingUiState) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Booking summary", style = MaterialTheme.typography.titleMedium)
            SummaryRow("Service", service.name)
            SummaryRow("Provider", service.provider)
            SummaryRow("Date", uiState.date)
            SummaryRow("Time", uiState.slot?.let { "${it.startTime} - ${it.endTime}" } ?: "Unavailable")
            SummaryRow("Price", "${service.currency} ${service.price}")
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun BookingConfirmation(bookingNumber: String, onDone: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "Your booking is confirmed!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Booking number: $bookingNumber",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(onClick = onDone, modifier = Modifier.padding(top = 32.dp)) {
            Text("View My Bookings")
        }
    }
}


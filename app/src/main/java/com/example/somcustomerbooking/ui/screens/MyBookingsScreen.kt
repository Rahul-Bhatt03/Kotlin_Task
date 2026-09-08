package com.example.somcustomerbooking.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.somcustomerbooking.model.Booking
import com.example.somcustomerbooking.model.BookingStatus
import com.example.somcustomerbooking.model.UiState
import com.example.somcustomerbooking.ui.components.EmptyView
import com.example.somcustomerbooking.ui.components.ErrorView
import com.example.somcustomerbooking.ui.components.LoadingView
import com.example.somcustomerbooking.viewmodel.MyBookingsViewModel
import com.example.somcustomerbooking.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onBack: () -> Unit,
    viewModel: MyBookingsViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is UiState.Loading -> LoadingView()
                is UiState.Empty -> EmptyView("You don't have any bookings yet.")
                is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::retry)
                is UiState.Success -> BookingList(state.data)
            }
        }
    }
}

@Composable
private fun BookingList(bookings: List<Booking>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(bookings, key = { it.id }) { booking ->
            BookingCard(booking)
        }
    }
}

@Composable
private fun BookingCard(booking: Booking) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = booking.bookingNumber, style = MaterialTheme.typography.titleMedium)
                StatusBadge(booking.status)
            }
            Text(text = booking.serviceName, modifier = Modifier.padding(top = 4.dp))
            Text(text = booking.provider, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${booking.scheduledDate} at ${booking.scheduledTime}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun StatusBadge(status: BookingStatus) {
    val label = when (status) {
        BookingStatus.CONFIRMED -> "Confirmed"
        BookingStatus.PENDING -> "Pending"
        BookingStatus.CANCELLED -> "Cancelled"
        BookingStatus.COMPLETED -> "Completed"
    }
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

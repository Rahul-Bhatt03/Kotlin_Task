package com.example.somcustomerbooking.ui.screens.service

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.somcustomerbooking.model.Service
import com.example.somcustomerbooking.model.UiState
import com.example.somcustomerbooking.ui.components.EmptyView
import com.example.somcustomerbooking.ui.components.ErrorView
import com.example.somcustomerbooking.ui.components.LoadingView
import com.example.somcustomerbooking.viewmodel.ServiceListViewModel
import com.example.somcustomerbooking.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    onServiceClick: (String) -> Unit,
    onMyBookingsClick: () -> Unit,
    viewModel: ServiceListViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Services") })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChanged,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search services, category or provider") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                // Simple text-link affordance rather than a full button;
                Text(
                    text = "My Bookings",
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable(onClick = onMyBookingsClick),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    style = androidx.compose.material3.MaterialTheme.typography.labelLarge
                )
            }

            when (val state = uiState.listState) {
                is UiState.Loading -> LoadingView()
                is UiState.Empty -> EmptyView("No services match your search.")
                is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::retry)
                is UiState.Success -> ServiceList(services = state.data, onServiceClick = onServiceClick)
            }
        }
    }
}

@Composable
private fun ServiceList(
    services: List<Service>,
    onServiceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(services, key = { it.id }) { service ->
            ServiceCard(service = service, onClick = { onServiceClick(service.id) })
        }
    }
}

@Composable
private fun ServiceCard(service: Service, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = service.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(
                text = "${service.category} • ${service.provider}",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${service.currency} ${service.price} • ${service.durationMinutes} min")
                Row {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = service.rating.toString())
                }
            }
        }
    }
}

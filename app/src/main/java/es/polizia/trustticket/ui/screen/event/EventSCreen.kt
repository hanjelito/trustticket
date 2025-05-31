package es.polizia.trustticket.ui.screen.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.polizia.trustticket.ui.components.EventCard
import es.polizia.trustticket.ui.viewModel.EventsViewModel
import es.polizia.trustticket.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    onEventClick: (String) -> Unit,
    viewModel: EventsViewModel = viewModel()
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo de TrustTicket",
                            modifier = Modifier
                                .height(40.dp)
                                .width(40.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.texto),
                            contentDescription = "Logo de TrustTicket",
                            modifier = Modifier
                                .height(40.dp)
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // importante para respetar el padding del Scaffold
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        isLoading && events.isEmpty() -> {
                            LoadingState()
                        }
                        events.isEmpty() && !isLoading -> {
                            EmptyState(onRetry = { viewModel.refreshEvents() })
                        }
                        else -> {
                            EventsList(
                                events = events,
                                onEventClick = onEventClick,
                                isRefreshing = isLoading && events.isNotEmpty()
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading events...")
        }
    }
}

@Composable
private fun EmptyState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "No events found",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Check your connection and try again",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EventsList(
    events: List<es.polizia.trustticket.data.dto.EventDTO>,
    onEventClick: (String) -> Unit,
    isRefreshing: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = events,
                key = { event -> event.id }
            ) { event ->
                EventCard(
                    event = event,
                    onClick = onEventClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            if (isRefreshing) {
                item {
                    Spacer(modifier = Modifier.height(56.dp))
                }
            }
        }

        if (isRefreshing) {
            RefreshIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun RefreshIndicator(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Updating...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

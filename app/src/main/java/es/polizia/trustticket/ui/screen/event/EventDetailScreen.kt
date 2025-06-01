package es.polizia.trustticket.ui.screen.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import es.polizia.trustticket.data.dto.EventDTO
import es.polizia.trustticket.data.repository.TicketRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EventDetailScreen(
    event: EventDTO,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TopAppBar con botón de regreso
        TopAppBar(
            title = { Text("Event Details") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        // Contenido scrolleable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Imagen del evento
            SubcomposeAsyncImage(
                model = event.imageUrl,
                contentDescription = "Image for ${event.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🖼️",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Text(
                                text = "Image not available",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            )

            // Contenido del evento
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Título del evento
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Categoría
                if (event.category.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = event.category,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Precio
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = "💰 ${event.priceMin}€ - ${event.priceMax}€",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                if (event.description.isNotEmpty()) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Información de fecha y hora
                InfoCard(
                    icon = Icons.Default.DateRange,
                    title = "Date & Time",
                    content = formatDateTime(event.startDatetime, event.endDatetime)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Información de ubicación
                InfoCard(
                    icon = Icons.Default.LocationOn,
                    title = "Location",
                    content = buildString {
                        append(event.locationName)
                        if (event.locationAddress.isNotEmpty()) {
                            append("\n${event.locationAddress}")
                        }
                        if (event.nearestMetro.isNotEmpty()) {
                            append("\n🚇 ${event.nearestMetro}")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Información del organizador
                Text(
                    text = "Organizer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.organizer,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Información de contacto
                if (event.contactPhone.isNotEmpty() || event.contactEmail.isNotEmpty() || event.contactWebsite.isNotEmpty()) {
                    Text(
                        text = "Contact Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (event.contactPhone.isNotEmpty()) {
                        ContactRow(
                            icon = Icons.Default.Phone,
                            label = "Phone",
                            value = event.contactPhone
                        )
                    }

                    if (event.contactEmail.isNotEmpty()) {
                        ContactRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = event.contactEmail
                        )
                    }

                    if (event.contactWebsite.isNotEmpty()) {
                        ContactRow(
                            icon = Icons.Default.Info,
                            label = "Website",
                            value = event.contactWebsite
                        )
                    }
                }

                // Tags
                if (event.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tags",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Tags con FlowRow para ajuste automático
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        event.tags.forEach { tag ->
                            Surface(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "#$tag",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Espacio final
                Spacer(modifier = Modifier.height(16.dp))

                // Botón de comprar
                Button(
                    onClick = {
                        println("🔵 Botón presionado - iniciando compra")
                        isLoading = true
                        showErrorMessage = false
                        coroutineScope.launch {
                            try {
                                println("🔵 Creando TicketRepository")
                                val ticketRepository = TicketRepository()
                                println("🔵 Llamando buyTicket con eventId: ${event.id}")
                                val success = ticketRepository.buyTicket(event.id, "A12")
                                println("🔵 Resultado de buyTicket: $success")

                                // TEMPORAL: Para testing, forzamos success = true
                                // Comenta esta línea cuando el backend funcione
                                val finalSuccess = true // <-- LÍNEA TEMPORAL PARA TESTING

                                if (finalSuccess) {
                                    println("✅ Compra exitosa - mostrando modal")
                                    showSuccessModal = true
                                    showErrorMessage = false
                                } else {
                                    println("❌ Compra falló - success = false")
                                    errorMessage = "Error al comprar el ticket"
                                    showErrorMessage = true
                                }
                            } catch (e: Exception) {
                                println("❌ Excepción en compra: ${e.message}")
                                e.printStackTrace()
                                errorMessage = "Error: ${e.localizedMessage ?: "Error desconocido"}"
                                showErrorMessage = true
                            } finally {
                                println("🔵 Finalizando compra - isLoading = false")
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Comprando...")
                    } else {
                        Text(
                            text = "🎫 Comprar Ticket - ${event.priceMin}€",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Modal de éxito
        if (showSuccessModal) {
            println("🎉 Mostrando modal de éxito")
            PurchaseSuccessModal(
                event = event,
                onDismiss = {
                    println("🎉 Modal cerrado - regresando a eventos")
                    showSuccessModal = false
                    onBack() // Regresa a la lista de eventos
                }
            )
        }

        // Snackbar para mensajes de error
        if (showErrorMessage) {
            LaunchedEffect(showErrorMessage) {
                kotlinx.coroutines.delay(3000)
                showErrorMessage = false
            }
        }

        // Mostrar Snackbars solo para errores
        if (showErrorMessage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = "❌ $errorMessage",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun PurchaseSuccessModal(
    event: EventDTO,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Aceptar")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎉 ¡Gracias por tu compra!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Detalles de tu ticket:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nombre del evento
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "🎪 ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = event.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fecha
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "📅 ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = formatDateTimeForModal(event.startDatetime),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Ubicación
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "📍 ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = event.locationName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Asiento
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "🪑 ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Asiento: A12",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Precio
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "💰 ",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Precio: ${event.priceMin}€",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tu ticket ha sido confirmado. Recibirás un email con todos los detalles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatDateTime(startDateTime: String, endDateTime: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm", Locale.ENGLISH)

        val start = LocalDateTime.parse(startDateTime, formatter)
        val end = LocalDateTime.parse(endDateTime, formatter)

        val startFormatted = start.format(displayFormatter)
        val endFormatted = end.format(displayFormatter)

        "From: $startFormatted\nTo: $endFormatted"
    } catch (e: Exception) {
        "From: $startDateTime\nTo: $endDateTime"
    }
}

private fun formatDateTimeForModal(startDateTime: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm", Locale.ENGLISH)

        val start = LocalDateTime.parse(startDateTime, formatter)
        start.format(displayFormatter)
    } catch (e: Exception) {
        startDateTime
    }
}
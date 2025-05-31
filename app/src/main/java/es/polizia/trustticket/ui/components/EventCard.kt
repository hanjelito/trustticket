package es.polizia.trustticket.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import es.polizia.trustticket.data.dto.EventDTO
import es.polizia.trustticket.ui.screen.event.components.EventContentCard

@Composable
fun EventCard(
    event: EventDTO,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Memoizar el gradiente para evitar recrearlo en cada recomposición
    val gradientBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.3f),
                Color.Black.copy(alpha = 0.8f)
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(event.id.toString()) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp, // Reducido para mejor rendimiento
            pressedElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            EventImage(
                imageUrl = event.imageUrl,
                eventName = event.name,
                modifier = Modifier.fillMaxSize()
            )

            // Usar drawBehind en lugar de Box con background para mejor rendimiento
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawGradientOverlay(gradientBrush)
                    }
            )

            EventContentCard(
                event = event,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            )
        }
    }
}

@Composable
private fun EventImage(
    imageUrl: String?,
    eventName: String,
    modifier: Modifier = Modifier
) {
    // Obtener colores del tema y luego memoizar el gradiente
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val errorGradientBrush = remember(surfaceVariant) {
        Brush.verticalGradient(
            colors = listOf(
                surfaceVariant,
                surfaceVariant.copy(alpha = 0.8f)
            )
        )
    }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = "Imagen del evento $eventName",
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
        loading = {
            LoadingImagePlaceholder()
        },
        error = {
            ErrorImagePlaceholder(surfaceVariant = surfaceVariant)
        }
    )
}

@Composable
private fun LoadingImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ErrorImagePlaceholder(surfaceVariant: Color) {
    val gradientBrush = remember(surfaceVariant) {
        Brush.verticalGradient(
            colors = listOf(
                surfaceVariant,
                surfaceVariant.copy(alpha = 0.8f)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎪",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Imagen no disponible",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Función de extensión para dibujar el gradiente de manera más eficiente
private fun DrawScope.drawGradientOverlay(brush: Brush) {
    drawRect(brush = brush)
}
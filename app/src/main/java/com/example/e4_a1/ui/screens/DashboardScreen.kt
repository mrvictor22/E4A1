package com.example.e4_a1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e4_a1.data.models.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    state: MonitoringState,
    onToggleMonitoring: () -> Unit,
    onResetStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con estado de monitoreo
        item {
            MonitoringHeader(
                isMonitoring = state.isMonitoring,
                onToggle = onToggleMonitoring,
                hasPermission = state.hasLocationPermission,
                isGpsEnabled = state.isGpsEnabled
            )
        }
        
        // Score de seguridad
        item {
            SafetyScoreCard(score = state.statistics.safetyScore)
        }
        
        // Datos en tiempo real
        item {
            RealTimeDataCard(
                locationData = state.locationData,
                accelerometerData = state.accelerometerData,
                currentEvent = state.currentEvent
            )
        }
        
        // Estadísticas generales
        item {
            StatisticsCard(
                statistics = state.statistics,
                onReset = onResetStats
            )
        }
        
        // Eventos recientes
        item {
            Text(
                text = "Eventos Recientes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (state.recentEvents.isEmpty()) {
            item {
                EmptyEventsCard()
            }
        } else {
            items(state.recentEvents) { event ->
                EventCard(event = event)
            }
        }
    }
}

@Composable
fun MonitoringHeader(
    isMonitoring: Boolean,
    onToggle: () -> Unit,
    hasPermission: Boolean,
    isGpsEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isMonitoring) 
                MaterialTheme.colorScheme.primaryContainer 
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SafeDrive Guardian",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isMonitoring) "Monitoreo Activo" else "Monitoreo Inactivo",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isMonitoring) 
                    MaterialTheme.colorScheme.primary 
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de inicio/parada
            FloatingActionButton(
                onClick = onToggle,
                modifier = Modifier.size(72.dp),
                containerColor = if (isMonitoring) 
                    MaterialTheme.colorScheme.error 
                else MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (isMonitoring) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                    contentDescription = if (isMonitoring) "Detener" else "Iniciar",
                    modifier = Modifier.size(36.dp)
                )
            }
            
            // Advertencias
            if (!hasPermission || !isGpsEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                
                if (!hasPermission) {
                    WarningChip(
                        text = "Se requieren permisos de ubicación",
                        icon = Icons.Filled.Warning
                    )
                }
                
                if (!isGpsEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    WarningChip(
                        text = "GPS desactivado",
                        icon = Icons.Filled.LocationOff
                    )
                }
            }
        }
    }
}

@Composable
fun SafetyScoreCard(score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                score >= 80 -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                score >= 60 -> Color(0xFFFFC107).copy(alpha = 0.1f)
                else -> Color(0xFFF44336).copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Score de Seguridad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Círculo de score animado
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { score / 100f },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = when {
                        score >= 80 -> Color(0xFF4CAF50)
                        score >= 60 -> Color(0xFFFFC107)
                        else -> Color(0xFFF44336)
                    },
                )
                
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        score >= 80 -> Color(0xFF4CAF50)
                        score >= 60 -> Color(0xFFFFC107)
                        else -> Color(0xFFF44336)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when {
                    score >= 80 -> "Excelente Conducción"
                    score >= 60 -> "Conducción Aceptable"
                    else -> "Conducción Peligrosa"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun RealTimeDataCard(
    locationData: LocationData,
    accelerometerData: AccelerometerData,
    currentEvent: DrivingEventType?
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Datos en Tiempo Real",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DataItem(
                    icon = Icons.Filled.Speed,
                    label = "Velocidad",
                    value = String.format("%.1f km/h", locationData.speedKmh),
                    color = if (locationData.speedKmh > 80) Color(0xFFF44336) else Color(0xFF4CAF50)
                )
                
                DataItem(
                    icon = Icons.Filled.TrendingUp,
                    label = "Aceleración",
                    value = String.format("%.1f m/s²", accelerometerData.magnitude),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DataItem(
                    icon = Icons.Filled.MyLocation,
                    label = "Precisión GPS",
                    value = String.format("±%.0f m", locationData.accuracy),
                    color = MaterialTheme.colorScheme.secondary
                )
                
                DataItem(
                    icon = Icons.Filled.Landscape,
                    label = "Altitud",
                    value = String.format("%.0f m", locationData.altitude),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            
            // Evento actual
            AnimatedVisibility(visible = currentEvent != null) {
                currentEvent?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(it.severity.color).copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = Color(it.severity.color)
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = it.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(it.severity.color)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DataItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun StatisticsCard(
    statistics: DrivingStatistics,
    onReset: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estadísticas del Viaje",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(onClick = onReset) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reiniciar estadísticas"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            StatRow("Distancia Total", statistics.getTotalDistanceString())
            StatRow("Tiempo Total", statistics.getTotalTimeString())
            StatRow("Velocidad Promedio", statistics.getAverageSpeedString())
            StatRow("Velocidad Máxima", statistics.getMaxSpeedString())
            
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            
            Text(
                text = "Eventos Detectados",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            EventStatRow("Frenazos Bruscos", statistics.harshBrakingCount, Color(0xFFFF9800))
            EventStatRow("Aceleraciones Agresivas", statistics.harshAccelerationCount, Color(0xFFFF9800))
            EventStatRow("Giros Violentos", statistics.sharpTurnCount, Color(0xFFFF9800))
            EventStatRow("Excesos de Velocidad", statistics.speedingCount, Color(0xFFFFC107))
            EventStatRow("Posibles Impactos", statistics.possibleCrashCount, Color(0xFFF44336))
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EventStatRow(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (count > 0) color else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EventCard(event: DrivingEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(event.type.severity.color).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (event.type) {
                    DrivingEventType.HARSH_BRAKING -> Icons.Filled.Warning
                    DrivingEventType.HARSH_ACCELERATION -> Icons.Filled.TrendingUp
                    DrivingEventType.SHARP_TURN -> Icons.Filled.TurnLeft
                    DrivingEventType.POSSIBLE_CRASH -> Icons.Filled.Error
                    DrivingEventType.SPEEDING -> Icons.Filled.Speed
                    else -> Icons.Filled.Check
                },
                contentDescription = null,
                tint = Color(event.type.severity.color),
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.type.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(event.type.severity.color)
                )
                
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = formatTimestamp(event.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyEventsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Sin eventos detectados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "¡Mantén una conducción segura!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WarningChip(text: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFC107).copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFFC107)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

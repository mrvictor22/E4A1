package com.example.e4_a1.data.models

import androidx.compose.ui.graphics.Color

/**
 * Representa los datos de ubicación GPS en tiempo real
 */
data class LocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val speed: Float = 0f, // m/s
    val speedKmh: Float = 0f, // km/h
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Representa los datos del acelerómetro
 */
data class AccelerometerData(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val magnitude: Float = 0f, // Magnitud del vector de aceleración
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Tipos de eventos de conducción detectados
 */
enum class DrivingEventType(val displayName: String, val severity: EventSeverity) {
    HARSH_BRAKING("Frenado Brusco", EventSeverity.WARNING),
    HARSH_ACCELERATION("Aceleración Agresiva", EventSeverity.WARNING),
    SHARP_TURN("Giro Violento", EventSeverity.WARNING),
    POSSIBLE_CRASH("Posible Impacto", EventSeverity.CRITICAL),
    SPEEDING("Exceso de Velocidad", EventSeverity.CAUTION),
    SMOOTH_DRIVING("Conducción Suave", EventSeverity.NORMAL)
}

/**
 * Severidad de los eventos
 */
enum class EventSeverity(val color: Long) {
    NORMAL(0xFF4CAF50),    // Verde
    CAUTION(0xFFFFC107),   // Amarillo
    WARNING(0xFFFF9800),   // Naranja
    CRITICAL(0xFFF44336)   // Rojo
}

/**
 * Representa un evento de conducción detectado
 */
data class DrivingEvent(
    val id: String = System.currentTimeMillis().toString(),
    val type: DrivingEventType,
    val timestamp: Long = System.currentTimeMillis(),
    val location: LocationData,
    val accelerometerData: AccelerometerData,
    val description: String = ""
)

/**
 * Estadísticas de conducción
 */
data class DrivingStatistics(
    val totalDistance: Float = 0f, // km
    val totalTime: Long = 0L, // milliseconds
    val averageSpeed: Float = 0f, // km/h
    val maxSpeed: Float = 0f, // km/h
    val harshBrakingCount: Int = 0,
    val harshAccelerationCount: Int = 0,
    val sharpTurnCount: Int = 0,
    val possibleCrashCount: Int = 0,
    val speedingCount: Int = 0,
    val safetyScore: Int = 100 // 0-100
) {
    fun getTotalEvents(): Int = harshBrakingCount + harshAccelerationCount + 
                                 sharpTurnCount + possibleCrashCount + speedingCount
    
    fun getAverageSpeedString(): String = String.format("%.1f km/h", averageSpeed)
    
    fun getMaxSpeedString(): String = String.format("%.1f km/h", maxSpeed)
    
    fun getTotalDistanceString(): String = String.format("%.2f km", totalDistance)
    
    fun getTotalTimeString(): String {
        val hours = totalTime / (1000 * 60 * 60)
        val minutes = (totalTime % (1000 * 60 * 60)) / (1000 * 60)
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }
}

/**
 * Estado del sistema de monitoreo
 */
data class MonitoringState(
    val isMonitoring: Boolean = false,
    val locationData: LocationData = LocationData(),
    val accelerometerData: AccelerometerData = AccelerometerData(),
    val currentEvent: DrivingEventType? = null,
    val recentEvents: List<DrivingEvent> = emptyList(),
    val statistics: DrivingStatistics = DrivingStatistics(),
    val hasLocationPermission: Boolean = false,
    val isGpsEnabled: Boolean = false,
    val isSensorAvailable: Boolean = false
)

package com.example.e4_a1.data.repository

import android.content.Context
import com.example.e4_a1.data.models.*
import com.example.e4_a1.data.sensors.AccelerometerService
import com.example.e4_a1.data.sensors.LocationService
import kotlinx.coroutines.flow.*

/**
 * Repositorio que coordina los servicios de sensores y gestiona los datos de conducción
 */
class DrivingRepository(context: Context) {
    
    private val locationService = LocationService(context)
    private val accelerometerService = AccelerometerService(context)
    
    // Estado actual del monitoreo
    private val _monitoringState = MutableStateFlow(MonitoringState())
    val monitoringState: StateFlow<MonitoringState> = _monitoringState.asStateFlow()
    
    // Lista de eventos detectados
    private val _events = MutableStateFlow<List<DrivingEvent>>(emptyList())
    val events: StateFlow<List<DrivingEvent>> = _events.asStateFlow()
    
    // Variables para cálculo de estadísticas
    private var tripStartTime = 0L
    private var lastLocation: LocationData? = null
    private var totalDistance = 0f
    private val accelerationHistory = mutableListOf<Float>()
    
    /**
     * Verifica permisos y disponibilidad de sensores
     */
    fun checkSystemStatus(): MonitoringState {
        val hasLocationPermission = locationService.hasLocationPermission()
        val isGpsEnabled = locationService.isGpsEnabled()
        val isSensorAvailable = accelerometerService.isAvailable()
        
        _monitoringState.value = _monitoringState.value.copy(
            hasLocationPermission = hasLocationPermission,
            isGpsEnabled = isGpsEnabled,
            isSensorAvailable = isSensorAvailable
        )
        
        return _monitoringState.value
    }
    
    /**
     * Inicia el monitoreo de conducción
     */
    fun startMonitoring() = combine(
        locationService.startLocationUpdates(),
        accelerometerService.startMonitoring()
    ) { locationData, accelerometerData ->
        
        // Actualizar historial de aceleración
        if (accelerationHistory.size > 100) {
            accelerationHistory.removeAt(0)
        }
        accelerationHistory.add(accelerometerData.magnitude)
        
        // Calcular distancia recorrida
        lastLocation?.let { last ->
            val distance = locationService.calculateDistance(
                last.latitude, last.longitude,
                locationData.latitude, locationData.longitude
            )
            totalDistance += distance / 1000f // Convertir a km
        }
        lastLocation = locationData
        
        // Detectar eventos de conducción
        val detectedEvent = accelerometerService.detectDrivingEvent(
            accelerometerData,
            locationData.speedKmh
        )
        
        // Detectar exceso de velocidad
        val speedingDetected = locationService.isSpeedingDetected(
            locationData.speedKmh,
            locationData
        )
        
        val eventType = when {
            detectedEvent != null -> detectedEvent
            speedingDetected && locationData.speedKmh > 50 -> DrivingEventType.SPEEDING
            else -> null
        }
        
        // Registrar evento si es significativo
        if (eventType != null && eventType != DrivingEventType.SMOOTH_DRIVING) {
            val event = DrivingEvent(
                type = eventType,
                location = locationData,
                accelerometerData = accelerometerData,
                description = getEventDescription(eventType, locationData, accelerometerData)
            )
            addEvent(event)
        }
        
        // Actualizar estadísticas
        updateStatistics(locationData, eventType)
        
        // Actualizar estado
        _monitoringState.value = _monitoringState.value.copy(
            isMonitoring = true,
            locationData = locationData,
            accelerometerData = accelerometerData,
            currentEvent = eventType
        )
        
        Pair(locationData, accelerometerData)
    }
    
    /**
     * Detiene el monitoreo
     */
    fun stopMonitoring() {
        _monitoringState.value = _monitoringState.value.copy(
            isMonitoring = false,
            currentEvent = null
        )
        tripStartTime = 0L
        lastLocation = null
        totalDistance = 0f
        accelerationHistory.clear()
    }
    
    /**
     * Añade un evento a la lista
     */
    private fun addEvent(event: DrivingEvent) {
        val currentEvents = _events.value.toMutableList()
        currentEvents.add(0, event) // Añadir al inicio
        
        // Mantener solo los últimos 50 eventos
        if (currentEvents.size > 50) {
            currentEvents.removeLast()
        }
        
        _events.value = currentEvents
        
        // Actualizar eventos recientes en el estado
        _monitoringState.value = _monitoringState.value.copy(
            recentEvents = currentEvents.take(10)
        )
    }
    
    /**
     * Actualiza las estadísticas de conducción
     */
    private fun updateStatistics(locationData: LocationData, eventType: DrivingEventType?) {
        if (tripStartTime == 0L) {
            tripStartTime = System.currentTimeMillis()
        }
        
        val currentStats = _monitoringState.value.statistics
        val totalTime = System.currentTimeMillis() - tripStartTime
        
        val newStats = currentStats.copy(
            totalDistance = totalDistance,
            totalTime = totalTime,
            averageSpeed = if (totalTime > 0) {
                (totalDistance / (totalTime / 1000f / 3600f))
            } else 0f,
            maxSpeed = maxOf(currentStats.maxSpeed, locationData.speedKmh),
            harshBrakingCount = if (eventType == DrivingEventType.HARSH_BRAKING) 
                currentStats.harshBrakingCount + 1 else currentStats.harshBrakingCount,
            harshAccelerationCount = if (eventType == DrivingEventType.HARSH_ACCELERATION)
                currentStats.harshAccelerationCount + 1 else currentStats.harshAccelerationCount,
            sharpTurnCount = if (eventType == DrivingEventType.SHARP_TURN)
                currentStats.sharpTurnCount + 1 else currentStats.sharpTurnCount,
            possibleCrashCount = if (eventType == DrivingEventType.POSSIBLE_CRASH)
                currentStats.possibleCrashCount + 1 else currentStats.possibleCrashCount,
            speedingCount = if (eventType == DrivingEventType.SPEEDING)
                currentStats.speedingCount + 1 else currentStats.speedingCount,
            safetyScore = calculateSafetyScore(currentStats, eventType)
        )
        
        _monitoringState.value = _monitoringState.value.copy(statistics = newStats)
    }
    
    /**
     * Calcula el score de seguridad (0-100)
     */
    private fun calculateSafetyScore(stats: DrivingStatistics, newEvent: DrivingEventType?): Int {
        var score = 100
        
        // Penalizaciones por eventos
        score -= stats.harshBrakingCount * 5
        score -= stats.harshAccelerationCount * 4
        score -= stats.sharpTurnCount * 4
        score -= stats.possibleCrashCount * 20
        score -= stats.speedingCount * 3
        
        // Bonus por conducción suave
        val smoothnessScore = accelerometerService.calculateSmoothnessScore(
            accelerationHistory
        )
        score = (score + smoothnessScore) / 2
        
        return score.coerceIn(0, 100)
    }
    
    /**
     * Genera descripción del evento
     */
    private fun getEventDescription(
        eventType: DrivingEventType,
        location: LocationData,
        accel: AccelerometerData
    ): String {
        return when (eventType) {
            DrivingEventType.HARSH_BRAKING -> 
                "Frenado brusco detectado a ${String.format("%.1f", location.speedKmh)} km/h"
            DrivingEventType.HARSH_ACCELERATION -> 
                "Aceleración agresiva detectada (${String.format("%.1f", accel.y)} m/s²)"
            DrivingEventType.SHARP_TURN -> 
                "Giro violento detectado a ${String.format("%.1f", location.speedKmh)} km/h"
            DrivingEventType.POSSIBLE_CRASH -> 
                "⚠️ POSIBLE IMPACTO DETECTADO - Magnitud: ${String.format("%.1f", accel.magnitude)} m/s²"
            DrivingEventType.SPEEDING -> 
                "Exceso de velocidad: ${String.format("%.1f", location.speedKmh)} km/h"
            DrivingEventType.SMOOTH_DRIVING -> 
                "Conducción suave"
        }
    }
    
    /**
     * Reinicia las estadísticas
     */
    fun resetStatistics() {
        tripStartTime = System.currentTimeMillis()
        totalDistance = 0f
        lastLocation = null
        accelerationHistory.clear()
        _events.value = emptyList()
        _monitoringState.value = _monitoringState.value.copy(
            statistics = DrivingStatistics(),
            recentEvents = emptyList()
        )
    }
}

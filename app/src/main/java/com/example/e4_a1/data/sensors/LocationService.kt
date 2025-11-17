package com.example.e4_a1.data.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.e4_a1.data.models.LocationData
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Servicio para gestionar la ubicación GPS
 */
class LocationService(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    companion object {
        const val UPDATE_INTERVAL_MS = 1000L        // 1 segundo
        const val FASTEST_INTERVAL_MS = 500L        // 0.5 segundos
        const val MIN_DISPLACEMENT_METERS = 2f      // 2 metros
        const val SPEED_LIMIT_HIGHWAY = 90f         // 90 km/h (ejemplo)
        const val SPEED_LIMIT_URBAN = 50f           // 50 km/h (ejemplo)
        const val SPEED_LIMIT_SCHOOL_ZONE = 30f     // 30 km/h (ejemplo)
    }
    
    /**
     * Verifica si los permisos de ubicación están otorgados
     */
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Verifica si el GPS está habilitado
     */
    fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    
    /**
     * Obtiene la última ubicación conocida
     */
    suspend fun getLastLocation(): LocationData? {
        if (!hasLocationPermission()) return null
        
        return try {
            var locationData: LocationData? = null
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    locationData = it.toLocationData()
                }
            }
            locationData
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Inicia el monitoreo de ubicación GPS
     * @return Flow de datos de ubicación
     */
    fun startLocationUpdates(): Flow<LocationData> = callbackFlow {
        if (!hasLocationPermission()) {
            close(IllegalStateException("Permisos de ubicación no otorgados"))
            return@callbackFlow
        }
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL_MS
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_INTERVAL_MS)
            setMinUpdateDistanceMeters(MIN_DISPLACEMENT_METERS)
            setWaitForAccurateLocation(false)
        }.build()
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location.toLocationData())
                }
            }
        }
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            close(e)
        }
        
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
    
    /**
     * Detiene el monitoreo de ubicación
     */
    fun stopLocationUpdates() {
        // El flow se encarga de remover las actualizaciones cuando se cancela
    }
    
    /**
     * Calcula la distancia entre dos puntos GPS (en metros)
     */
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    
    /**
     * Determina si se está excediendo el límite de velocidad
     * (Esto es simplificado - en una app real se usaría una API de mapas para obtener límites reales)
     */
    fun isSpeedingDetected(speedKmh: Float, locationData: LocationData): Boolean {
        // Simplificación: usar velocidad como proxy de tipo de zona
        // En producción, usarías Google Maps API o similar para obtener límites reales
        val estimatedSpeedLimit = when {
            speedKmh > 70 -> SPEED_LIMIT_HIGHWAY  // Asumimos autopista
            speedKmh > 40 -> SPEED_LIMIT_URBAN    // Asumimos zona urbana
            else -> SPEED_LIMIT_SCHOOL_ZONE       // Asumimos zona escolar
        }
        
        return speedKmh > estimatedSpeedLimit * 1.1f // 10% de margen
    }
    
    /**
     * Convierte Location de Android a LocationData
     */
    private fun Location.toLocationData(): LocationData {
        val speedMs = this.speed // m/s
        val speedKmh = speedMs * 3.6f // Convertir a km/h
        
        return LocationData(
            latitude = this.latitude,
            longitude = this.longitude,
            speed = speedMs,
            speedKmh = speedKmh,
            altitude = this.altitude,
            accuracy = this.accuracy,
            timestamp = System.currentTimeMillis()
        )
    }
}

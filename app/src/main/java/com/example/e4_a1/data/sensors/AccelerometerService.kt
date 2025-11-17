package com.example.e4_a1.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.e4_a1.data.models.AccelerometerData
import com.example.e4_a1.data.models.DrivingEventType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.sqrt

/**
 * Servicio para gestionar el acelerómetro y detectar eventos de conducción
 */
class AccelerometerService(private val context: Context) {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    // Umbrales para detectar eventos (en m/s²)
    companion object {
        const val HARSH_BRAKING_THRESHOLD = 8.0f      // Frenado brusco
        const val HARSH_ACCELERATION_THRESHOLD = 6.0f  // Aceleración agresiva
        const val SHARP_TURN_THRESHOLD = 7.0f         // Giro violento (lateral)
        const val CRASH_THRESHOLD = 15.0f             // Posible impacto
        const val SMOOTH_DRIVING_THRESHOLD = 2.0f     // Conducción suave
        
        // Delay entre eventos del mismo tipo (para evitar spam)
        const val EVENT_COOLDOWN_MS = 3000L
    }
    
    private var lastEventTime = 0L
    private var lastEventType: DrivingEventType? = null
    
    /**
     * Verifica si el acelerómetro está disponible
     */
    fun isAvailable(): Boolean = accelerometer != null
    
    /**
     * Inicia el monitoreo del acelerómetro
     * @return Flow de datos del acelerómetro
     */
    fun startMonitoring(): Flow<AccelerometerData> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    
                    // Calcular magnitud del vector de aceleración
                    val magnitude = sqrt(x * x + y * y + z * z)
                    
                    val data = AccelerometerData(
                        x = x,
                        y = y,
                        z = z,
                        magnitude = magnitude,
                        timestamp = System.currentTimeMillis()
                    )
                    
                    trySend(data)
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No se requiere acción
            }
        }
        
        // Registrar listener con tasa de muestreo rápida
        accelerometer?.let {
            sensorManager.registerListener(
                listener,
                it,
                SensorManager.SENSOR_DELAY_GAME // ~20ms entre lecturas
            )
        }
        
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
    
    /**
     * Detiene el monitoreo del acelerómetro
     */
    fun stopMonitoring() {
        // El flow se encarga de desregistrar el listener cuando se cancela
    }
    
    /**
     * Detecta eventos de conducción basados en los datos del acelerómetro
     */
    fun detectDrivingEvent(
        data: AccelerometerData,
        currentSpeed: Float // km/h
    ): DrivingEventType? {
        val now = System.currentTimeMillis()
        
        // Evitar spam de eventos
        if (now - lastEventTime < EVENT_COOLDOWN_MS && lastEventType != null) {
            return null
        }
        
        // Restar la gravedad (9.8 m/s²) para obtener aceleración neta
        val netAcceleration = data.magnitude - SensorManager.GRAVITY_EARTH
        
        val event = when {
            // Detección de posible impacto (prioridad máxima)
            data.magnitude > CRASH_THRESHOLD -> {
                DrivingEventType.POSSIBLE_CRASH
            }
            
            // Frenado brusco: aceleración negativa significativa en eje Y
            // (asumiendo que el teléfono está en posición vertical)
            data.y < -HARSH_BRAKING_THRESHOLD && currentSpeed > 10 -> {
                DrivingEventType.HARSH_BRAKING
            }
            
            // Aceleración agresiva: aceleración positiva en eje Y
            data.y > HARSH_ACCELERATION_THRESHOLD && currentSpeed < 80 -> {
                DrivingEventType.HARSH_ACCELERATION
            }
            
            // Giro violento: aceleración lateral significativa
            kotlin.math.abs(data.x) > SHARP_TURN_THRESHOLD && currentSpeed > 20 -> {
                DrivingEventType.SHARP_TURN
            }
            
            // Conducción suave
            kotlin.math.abs(netAcceleration) < SMOOTH_DRIVING_THRESHOLD && currentSpeed > 5 -> {
                DrivingEventType.SMOOTH_DRIVING
            }
            
            else -> null
        }
        
        if (event != null && event != DrivingEventType.SMOOTH_DRIVING) {
            lastEventTime = now
            lastEventType = event
        }
        
        return event
    }
    
    /**
     * Calcula un score de suavidad de conducción (0-100)
     * Basado en la variación de aceleración
     */
    fun calculateSmoothnessScore(accelerationHistory: List<Float>): Int {
        if (accelerationHistory.isEmpty()) return 100
        
        // Calcular varianza de la aceleración
        val mean = accelerationHistory.average().toFloat()
        val variance = accelerationHistory.map { (it - mean) * (it - mean) }.average()
        val stdDev = sqrt(variance).toFloat()
        
        // Mapear desviación estándar a score (menos variación = mejor score)
        val score = (100 - (stdDev * 10)).coerceIn(0f, 100f)
        return score.toInt()
    }
}

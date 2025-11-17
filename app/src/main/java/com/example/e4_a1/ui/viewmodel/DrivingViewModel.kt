package com.example.e4_a1.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.e4_a1.data.models.DrivingEvent
import com.example.e4_a1.data.models.MonitoringState
import com.example.e4_a1.data.repository.DrivingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel principal para la aplicación de monitoreo de conducción
 */
class DrivingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = DrivingRepository(application)
    
    // Estado del monitoreo
    val monitoringState: StateFlow<MonitoringState> = repository.monitoringState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MonitoringState()
        )
    
    // Eventos de conducción
    val events: StateFlow<List<DrivingEvent>> = repository.events
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private var monitoringJob: Job? = null
    
    init {
        checkSystemStatus()
    }
    
    /**
     * Verifica el estado del sistema (permisos, GPS, sensores)
     */
    fun checkSystemStatus() {
        repository.checkSystemStatus()
    }
    
    /**
     * Inicia el monitoreo de conducción
     */
    fun startMonitoring() {
        if (monitoringJob?.isActive == true) return
        
        monitoringJob = viewModelScope.launch {
            repository.startMonitoring()
                .catch { e ->
                    // Manejar errores
                    e.printStackTrace()
                }
                .collect { (location, accelerometer) ->
                    // Los datos se actualizan automáticamente en el estado
                }
        }
    }
    
    /**
     * Detiene el monitoreo de conducción
     */
    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
        repository.stopMonitoring()
    }
    
    /**
     * Reinicia las estadísticas de conducción
     */
    fun resetStatistics() {
        repository.resetStatistics()
    }
    
    /**
     * Alterna el estado de monitoreo
     */
    fun toggleMonitoring() {
        if (monitoringState.value.isMonitoring) {
            stopMonitoring()
        } else {
            startMonitoring()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}

# SafeDrive Guardian
## Documento Técnico-Descriptivo

---

### PORTADA
**Título de la Actividad**: Innovación Móvil con Acelerómetro y GPS  
**Nombre de la Aplicación**: SafeDrive Guardian  
**Nombres e Integrantes**: [Completar con tus datos]  
**Fecha**: Noviembre 2025

---

## RESUMEN EJECUTIVO

SafeDrive Guardian es una aplicación Android que utiliza **GPS** y **acelerómetro** para monitorear y mejorar la seguridad en la conducción. Detecta en tiempo real eventos como frenazos bruscos, aceleraciones agresivas, giros violentos y posibles impactos, proporcionando un score de seguridad dinámico y estadísticas detalladas del viaje.

**Propuesta de Valor**: Prevención de accidentes mediante retroalimentación inmediata sobre patrones de conducción peligrosos.

**Principales Desafíos**: Optimización de consumo energético, precisión de sensores, y privacidad de datos.

---

## 1. INTRODUCCIÓN

### Problemática
Los accidentes de tráfico son una de las principales causas de muerte a nivel mundial. Muchos conductores no son conscientes de sus hábitos de conducción agresivos o peligrosos hasta que ocurre un accidente.

### Relevancia de la Solución
SafeDrive Guardian proporciona **conciencia situacional en tiempo real**, permitiendo a los conductores:
- Identificar y corregir comportamientos peligrosos
- Recibir retroalimentación objetiva sobre su conducción
- Mejorar sus hábitos para prevenir accidentes
- Obtener evidencia en caso de incidentes

---

## 2. CONCEPTO DE LA APLICACIÓN

### Descripción
Aplicación de monitoreo inteligente que combina datos de **GPS** (ubicación, velocidad) y **acelerómetro** (movimiento, aceleración) para evaluar la seguridad de la conducción.

### Público Objetivo
- Conductores individuales que buscan mejorar sus hábitos
- Padres que monitorean conductores jóvenes
- Empresas con flotas vehiculares
- Escuelas de manejo como herramienta educativa
- Compañías de seguros para programas de descuentos

### Justificación Técnica

**Integración GPS + Acelerómetro = Solución Completa**

| Sensor | Información | Uso en SafeDrive |
|--------|-------------|------------------|
| **GPS** | Ubicación, velocidad, altitud | Detección de exceso de velocidad, cálculo de distancia, ubicación de eventos |
| **Acelerómetro** | Aceleración 3D (X,Y,Z) | Detección de frenazos, aceleraciones, giros, impactos |

**¿Por qué esta combinación es ventajosa?**
- El GPS solo indica "qué tan rápido", el acelerómetro indica "cómo" se conduce
- Juntos detectan patrones que ninguno podría detectar solo
- Correlación espacio-temporal de eventos críticos

---

## 3. ARQUITECTURA FUNCIONAL

### Diagrama de Alto Nivel
```
[Sensores Físicos]
    ↓
[Servicios Android]
    ├── LocationService (GPS)
    └── AccelerometerService
    ↓
[DrivingRepository] ← Procesamiento y análisis
    ↓
[DrivingViewModel] ← Gestión de estado
    ↓
[UI Compose] ← Visualización
```

### Funcionalidades Principales

#### 3.1 Monitoreo en Tiempo Real
- **Velocidad actual**: Conversión automática m/s → km/h
- **Aceleración**: Magnitud vectorial del acelerómetro
- **Precisión GPS**: Indicador de confiabilidad de datos
- **Eventos activos**: Alertas visuales de comportamiento actual

#### 3.2 Detección de Eventos
| Evento | Umbral | Condición |
|--------|--------|-----------|
| Frenado Brusco | -8.0 m/s² | Desaceleración en eje Y + velocidad > 10 km/h |
| Aceleración Agresiva | +6.0 m/s² | Aceleración en eje Y + velocidad < 80 km/h |
| Giro Violento | ±7.0 m/s² | Aceleración lateral (eje X) + velocidad > 20 km/h |
| Posible Impacto | 15.0 m/s² | Magnitud total del vector |
| Exceso de Velocidad | Variable | Velocidad > límite estimado |

#### 3.3 Score de Seguridad
Algoritmo de cálculo:
```
Score = 100 
  - (frenazos × 5)
  - (aceleraciones × 4)
  - (giros × 4)
  - (impactos × 20)
  - (excesos × 3)
  + bonus_suavidad
```

#### 3.4 Estadísticas
- Distancia total (km)
- Tiempo de viaje
- Velocidades (promedio/máxima)
- Contador por tipo de evento

---

## 4. PROTOTIPO VISUAL (INFOGRAFÍA)

### Flujo de Datos Completo

```
┌─────────────────┐
│  CAPTURA        │
├─────────────────┤
│ GPS             │ → Ubicación, velocidad cada 1s
│ Acelerómetro    │ → Aceleración X,Y,Z cada 20ms
└────────┬────────┘
         ↓
┌─────────────────┐
│  PROCESAMIENTO  │
├─────────────────┤
│ • Fusión datos  │
│ • Filtrado      │
│ • Detección     │
│   patrones      │
│ • Cálculo       │
│   estadísticas  │
└────────┬────────┘
         ↓
┌─────────────────┐
│  ANÁLISIS       │
├─────────────────┤
│ • Umbrales      │
│ • Clasificación │
│ • Score         │
└────────┬────────┘
         ↓
┌─────────────────┐
│  PRESENTACIÓN   │
├─────────────────┤
│ • Dashboard     │
│ • Alertas       │
│ • Estadísticas  │
│ • Historial     │
└─────────────────┘
```

### UI/UX Propuesta

**Pantalla Principal (Dashboard)**
- Header: Estado ON/OFF, botón de control
- Score Card: Visualización circular 0-100
- Datos en Tiempo Real: 4 métricas principales
- Estadísticas: Resumen del viaje
- Eventos Recientes: Lista scrollable

**Colores por Severidad**
- Verde: Normal (80-100)
- Amarillo: Precaución (60-79)
- Naranja: Advertencia
- Rojo: Crítico (0-59 o impactos)

---

## 5. ANÁLISIS DE VIABILIDAD Y PROPUESTA DE VALOR

### Viabilidad Técnica
✅ **Hardware**: Disponible en todos los smartphones modernos  
✅ **Software**: APIs nativas de Android bien documentadas  
✅ **Rendimiento**: Procesamiento eficiente en tiempo real

### Innovación
- Primera app que **correlaciona** GPS y acelerómetro para seguridad
- Algoritmo de score dinámico único
- Procesamiento 100% local (privacidad)

### Eficiencia
- Consumo optimizado: Updates GPS cada 1s (no continuo)
- Sin dependencia de internet
- Sin servidores externos

### Experiencia de Usuario
- Interfaz intuitiva con Material Design 3
- Retroalimentación visual inmediata
- Sin configuración compleja
- Modo oscuro/claro automático

### Oportunidad de Mercado
- **Segment 1**: Conductores conscientes de seguridad (100M+ usuarios potenciales)
- **Segment 2**: Flotas corporativas (B2B)
- **Segment 3**: Seguros con telemática

---

## 6. ANÁLISIS DE DESAFÍOS TÉCNICOS

### Desafío 1: Precisión y Calibración de Sensores

**Problema**: 
- GPS puede tener error de ±5-50m según condiciones
- Acelerómetro incluye gravedad (9.8 m/s²) en las lecturas
- Orientación del dispositivo variable

**Estrategias de Mitigación**:
1. **Filtrado de datos GPS**: Solo procesar si accuracy < 20m
2. **Compensación de gravedad**: Restar `SensorManager.GRAVITY_EARTH`
3. **Umbrales adaptativos**: Ajustar según contexto de velocidad
4. **Fusión de sensores**: Correlacionar ambas fuentes para validación cruzada

**Implementación**:
```kotlin
// Restar gravedad para aceleración neta
val netAcceleration = magnitude - SensorManager.GRAVITY_EARTH

// Validar precisión GPS
if (locationData.accuracy > 20f) {
    // Ignorar o marcar como no confiable
}
```

### Desafío 2: Optimización del Consumo de Energía

**Problema**: 
- GPS y sensores activos drenan batería rápidamente
- Procesamiento continuo consume CPU
- Uso prolongado en viajes largos

**Estrategias de Mitigación**:
1. **Intervalos optimizados**:
   - GPS: 1 segundo (no continuo)
   - Desplazamiento mínimo: 2 metros
2. **Tasa de muestreo eficiente**:
   - Acelerómetro: SENSOR_DELAY_GAME (~20ms)
3. **Procesamiento selectivo**:
   - Solo calcular cuando hay cambios significativos
4. **Modo ahorro**: Reducir frecuencia si batería < 20%

**Implementación**:
```kotlin
val locationRequest = LocationRequest.Builder(
    Priority.PRIORITY_HIGH_ACCURACY,
    UPDATE_INTERVAL_MS // 1000ms
).apply {
    setMinUpdateDistanceMeters(2f) // Evita updates innecesarios
}.build()
```

### Desafío 3: Procesamiento en Tiempo Real

**Problema**:
- Flujo constante de datos (acelerómetro: 50 lecturas/s)
- Necesidad de respuesta inmediata para alertas
- Cálculos complejos pueden causar lag en UI

**Estrategias de Mitigación**:
1. **Kotlin Coroutines y Flow**: Procesamiento asíncrono sin bloquear UI
2. **Arquitectura MVVM**: Separación de lógica y presentación
3. **Cooldown system**: Evitar procesar eventos duplicados (3s entre similares)
4. **Buffer limitado**: Mantener solo últimas 100 lecturas en memoria

**Implementación**:
```kotlin
// Flow reactivo no bloqueante
repository.startMonitoring()
    .catch { e -> /* manejar error */ }
    .collect { data -> /* actualizar UI */ }
```

### Otros Desafíos Identificados

#### 4. Seguridad y Privacidad
- **Solución**: Procesamiento 100% local, sin servidores
- **Implementación**: Datos solo en StateFlow (memoria), sin persistencia por defecto

#### 5. Escalabilidad
- **Solución**: Estructura modular permite agregar funcionalidades
- **Diseño**: Repository pattern facilita cambiar implementaciones

#### 6. Variabilidad de Dispositivos
- **Solución**: Verificación de sensores disponibles
- **Implementación**: Graceful degradation si falta algún sensor

---

## 7. CONCLUSIONES Y TRABAJO FUTURO

### Conclusiones
- SafeDrive Guardian demuestra la viabilidad de integrar GPS y acelerómetro para mejorar la seguridad vial
- La combinación de sensores proporciona información que ninguno podría ofrecer individualmente
- La arquitectura MVVM con Jetpack Compose permite un desarrollo mantenible y escalable
- Los desafíos técnicos identificados tienen soluciones probadas y efectivas

### Aprendizajes Clave
1. Importancia de optimizar consumo de recursos en apps de sensores
2. Necesidad de validación cruzada de datos de múltiples fuentes
3. Balance entre precisión y rendimiento
4. Valor de retroalimentación en tiempo real para cambio de comportamiento

### Trabajo Futuro

**Corto Plazo**:
- Persistencia de datos con Room Database
- Notificaciones audibles para alertas críticas
- Modo emergencia con llamada automática

**Mediano Plazo**:
- Integración con Google Maps para límites de velocidad reales
- Machine Learning para detección de patrones avanzados
- Reportes PDF exportables

**Largo Plazo**:
- Integración con OBD-II del vehículo
- Detección de fatiga por análisis de patrones
- Plataforma social con comparación de scores (opt-in)

---

## REFERENCIAS

1. Android Developers. (2024). "Sensors Overview". https://developer.android.com/guide/topics/sensors/sensors_overview
2. Google. (2024). "Location and Context APIs". https://developers.google.com/location-context
3. Material Design. (2024). "Material 3 Design System". https://m3.material.io/
4. Jetpack Compose. (2024). "Modern UI Toolkit for Android". https://developer.android.com/jetpack/compose
5. WHO. (2023). "Global Status Report on Road Safety". World Health Organization.

---

**Nota**: Este documento describe la implementación técnica completa de SafeDrive Guardian. El código fuente está disponible en el repositorio del proyecto con arquitectura MVVM, Jetpack Compose y Kotlin Coroutines.

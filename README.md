# SafeDrive Guardian 🚗🛡️

**Aplicación Android para Monitoreo Inteligente de Conducción Segura**

## 📋 Descripción

SafeDrive Guardian es una aplicación móvil innovadora que combina **GPS** y **acelerómetro** para monitorear y mejorar la seguridad en la conducción. Detecta patrones de conducción peligrosos en tiempo real y proporciona retroalimentación inmediata al conductor.

## ✨ Características Principales

### 🎯 Integración de Sensores

1. **GPS (Ubicación)**
   - Seguimiento de ubicación en tiempo real
   - Medición de velocidad (km/h)
   - Cálculo de distancia recorrida
   - Detección de altitud
   - Precisión de ubicación

2. **Acelerómetro (Movimiento)**
   - Detección de frenazos bruscos
   - Identificación de aceleraciones agresivas
   - Análisis de giros violentos
   - Detección de posibles impactos
   - Evaluación de suavidad de conducción

### 📊 Funcionalidades

#### Monitoreo en Tiempo Real
- **Velocidad Actual**: Muestra la velocidad en km/h
- **Aceleración**: Magnitud de la aceleración en m/s²
- **Precisión GPS**: Indica la exactitud de la ubicación
- **Altitud**: Altura sobre el nivel del mar

#### Detección de Eventos
El sistema detecta automáticamente:
- ⚠️ **Frenados Bruscos**: Desaceleración > 8 m/s²
- ⚡ **Aceleraciones Agresivas**: Aceleración > 6 m/s²
- 🔄 **Giros Violentos**: Aceleración lateral > 7 m/s²
- 🚨 **Posibles Impactos**: Magnitud > 15 m/s²
- 🚦 **Exceso de Velocidad**: Velocidad > límites estimados

#### Score de Seguridad
- Cálculo dinámico basado en eventos detectados
- Escala de 0-100 puntos
- Categorías:
  - ✅ **80-100**: Excelente conducción
  - ⚠️ **60-79**: Conducción aceptable
  - ❌ **0-59**: Conducción peligrosa

#### Estadísticas del Viaje
- **Distancia Total**: Kilómetros recorridos
- **Tiempo Total**: Duración del viaje
- **Velocidad Promedio**: km/h promedio
- **Velocidad Máxima**: Velocidad pico alcanzada
- **Contador de Eventos**: Desglose por tipo

#### Historial de Eventos
- Lista de eventos recientes con timestamp
- Descripción detallada de cada evento
- Indicadores visuales por severidad
- Información de ubicación y velocidad

## 🏗️ Arquitectura Técnica

### Stack Tecnológico
- **Lenguaje**: Kotlin
- **Framework UI**: Jetpack Compose
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Gestión de Estado**: StateFlow / Flow
- **Sensores**: Android Sensor Framework
- **Ubicación**: Google Play Services Location
- **Permisos**: Accompanist Permissions

### Estructura del Proyecto

```
app/
├── data/
│   ├── models/
│   │   └── DrivingData.kt          # Modelos de datos
│   ├── sensors/
│   │   ├── AccelerometerService.kt # Servicio del acelerómetro
│   │   └── LocationService.kt      # Servicio GPS
│   └── repository/
│       └── DrivingRepository.kt    # Coordinador de servicios
├── ui/
│   ├── screens/
│   │   ├── DashboardScreen.kt      # Pantalla principal
│   │   └── PermissionsScreen.kt    # Pantalla de permisos
│   ├── theme/
│   │   └── ...                     # Tema Material 3
│   └── viewmodel/
│       └── DrivingViewModel.kt     # ViewModel principal
└── MainActivity.kt                  # Actividad principal
```

### Componentes Clave

#### 1. AccelerometerService
- Monitoreo continuo del acelerómetro
- Tasa de muestreo: ~20ms (SENSOR_DELAY_GAME)
- Cálculo de magnitud vectorial
- Detección de patrones de conducción
- Sistema de cooldown para evitar spam de eventos

#### 2. LocationService
- Updates de GPS cada 1 segundo
- Desplazamiento mínimo: 2 metros
- Cálculo de velocidad en tiempo real
- Conversión automática m/s a km/h
- Detección de exceso de velocidad

#### 3. DrivingRepository
- Coordinación de ambos servicios
- Combinación de flujos de datos
- Cálculo de estadísticas en tiempo real
- Gestión de historial de eventos
- Cálculo de score de seguridad

#### 4. DrivingViewModel
- Gestión del ciclo de vida
- Exposición de estado reactivo
- Control de inicio/parada de monitoreo
- Reinicio de estadísticas

## 🎨 Interfaz de Usuario

### Pantallas

#### Pantalla de Permisos
- Solicitud de permisos necesarios
- Explicación clara del uso de cada permiso
- Acceso directo a configuración del sistema
- Indicadores de privacidad

#### Dashboard Principal
- **Header**: Estado de monitoreo y botón de control
- **Score Card**: Visualización circular del score de seguridad
- **Datos en Tiempo Real**: Velocidad, aceleración, GPS, altitud
- **Estadísticas**: Resumen del viaje actual
- **Eventos Recientes**: Lista de últimos 10 eventos

### Diseño Visual
- Material Design 3
- Colores adaptativos por severidad:
  - 🟢 Verde: Normal/Seguro
  - 🟡 Amarillo: Precaución
  - 🟠 Naranja: Advertencia
  - 🔴 Rojo: Crítico
- Animaciones suaves
- Modo oscuro/claro automático

## 🔒 Permisos Requeridos

```xml
<!-- Ubicación GPS -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Ubicación en segundo plano (Android 10+) -->
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

<!-- Servicio en primer plano -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />

<!-- Notificaciones (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## 🚀 Instalación y Uso

### Requisitos
- Android 7.0 (API 24) o superior
- Sensor de acelerómetro
- GPS habilitado
- Permisos de ubicación otorgados

### Pasos de Instalación
1. Clonar el repositorio
2. Abrir en Android Studio
3. Sincronizar Gradle
4. Ejecutar en dispositivo físico (preferible) o emulador con GPS

### Uso de la Aplicación
1. **Primera Ejecución**: Otorgar permisos de ubicación y notificaciones
2. **Iniciar Monitoreo**: Presionar el botón de reproducción
3. **Durante la Conducción**: La app monitoreará automáticamente
4. **Ver Estadísticas**: Observar el score y eventos en tiempo real
5. **Detener Monitoreo**: Presionar el botón de parada
6. **Reiniciar Estadísticas**: Usar el botón de refresh en la tarjeta de estadísticas

## 🔧 Configuración

### Umbrales de Detección (Personalizables)
```kotlin
// AccelerometerService.kt
const val HARSH_BRAKING_THRESHOLD = 8.0f      // m/s²
const val HARSH_ACCELERATION_THRESHOLD = 6.0f  // m/s²
const val SHARP_TURN_THRESHOLD = 7.0f         // m/s²
const val CRASH_THRESHOLD = 15.0f             // m/s²
```

### Configuración de GPS
```kotlin
// LocationService.kt
const val UPDATE_INTERVAL_MS = 1000L        // 1 segundo
const val FASTEST_INTERVAL_MS = 500L        // 0.5 segundos
const val MIN_DISPLACEMENT_METERS = 2f      // 2 metros
```

## 🧪 Desafíos Técnicos y Soluciones

### 1. Precisión y Calibración de Sensores
**Desafío**: El acelerómetro incluye la gravedad (9.8 m/s²)
**Solución**: Restar `SensorManager.GRAVITY_EARTH` para obtener aceleración neta

### 2. Consumo de Batería
**Desafío**: GPS y sensores consumen mucha energía
**Soluciones**:
- Tasa de actualización optimizada (1 segundo)
- Desplazamiento mínimo (2 metros)
- Detener automáticamente cuando no se usa

### 3. Spam de Eventos
**Desafío**: Múltiples eventos del mismo tipo en corto tiempo
**Solución**: Sistema de cooldown de 3 segundos entre eventos similares

### 4. Orientación del Dispositivo
**Desafío**: El teléfono puede estar en cualquier orientación
**Consideración**: Los umbrales se ajustan asumiendo posición vertical (caso común en auto)

### 5. Privacidad de Datos
**Solución**: 
- Procesamiento local, sin envío a servidores
- Datos solo en memoria (no persistencia)
- Usuario controla inicio/parada

### 6. Límites de Velocidad Dinámicos
**Desafío**: No hay acceso a límites de velocidad reales
**Solución Simplificada**: Estimación basada en velocidad actual
**Mejora Futura**: Integración con Google Maps Roads API

## 📱 Especificaciones Técnicas

### Dependencias Principales
```gradle
// ViewModel y Lifecycle
androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0
androidx.lifecycle:lifecycle-runtime-compose:2.7.0

// Navigation
androidx.navigation:navigation-compose:2.7.6

// Location Services
com.google.android.gms:play-services-location:21.1.0

// DataStore
androidx.datastore:datastore-preferences:1.0.0

// Permissions
com.google.accompanist:accompanist-permissions:0.32.0
```

### Versiones
- **compileSdk**: 34
- **minSdk**: 24
- **targetSdk**: 34
- **Kotlin**: 1.9+
- **Compose BOM**: Última versión

## 🎯 Propuesta de Valor

### Beneficios
1. **Seguridad Mejorada**: Conciencia en tiempo real de patrones peligrosos
2. **Educación**: Retroalimentación para mejorar hábitos de conducción
3. **Prevención**: Detección temprana de comportamientos de riesgo
4. **Emergencias**: Detección automática de posibles impactos
5. **Estadísticas**: Análisis objetivo de rendimiento de conducción

### Casos de Uso
- 👨‍👩‍👧‍👦 **Padres**: Monitorear conducción de conductores jóvenes
- 🚗 **Flotas**: Seguimiento de seguridad de conductores corporativos
- 📚 **Escuelas de Manejo**: Herramienta educativa
- 💼 **Seguros**: Potencial para descuentos por conducción segura
- 👤 **Uso Personal**: Autoevaluación y mejora continua

## 🔮 Trabajo Futuro

### Mejoras Planificadas
1. **Persistencia de Datos**: DataStore/Room para historial
2. **Notificaciones**: Alertas audibles/visuales en tiempo real
3. **Modo Emergencia**: Llamada/SMS automático en caso de impacto
4. **Mapas**: Visualización de ruta con eventos marcados
5. **Reportes**: Generación de informes PDF/compartibles
6. **Límites Reales**: Integración con APIs de mapas
7. **Machine Learning**: Detección de patrones más sofisticada
8. **Competición Social**: Comparación de scores (opcional)

### Características Avanzadas
- Detección de fatiga (análisis de patrones)
- Integración con OBD-II del vehículo
- Modo carpool/compartir viaje
- Gamificación con logros
- Análisis de condiciones climáticas

## 📄 Licencia

Este proyecto fue desarrollado como parte de la actividad académica E4-A1 "Innovación Móvil con Acelerómetro y GPS".

## 👨‍💻 Autor

Desarrollado con ❤️ usando Kotlin y Jetpack Compose

---

**SafeDrive Guardian** - Conducción segura a través de la tecnología 🚗✨

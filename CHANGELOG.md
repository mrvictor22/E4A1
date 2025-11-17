# Changelog

Registro de cambios del proyecto SafeDrive Guardian

## [Unreleased]

### Added

- Aplicación completa de monitoreo de conducción segura con GPS y acelerómetro
- Sistema de detección de eventos de conducción (frenazos, aceleraciones, giros, impactos)
- Dashboard principal con datos en tiempo real
- Score de seguridad dinámico (0-100)
- Estadísticas de viaje (distancia, tiempo, velocidades)
- Historial de eventos recientes
- Pantalla de solicitud de permisos con Accompanist
- Servicio de acelerómetro con detección inteligente de patrones
- Servicio de GPS con Google Play Services Location
- Repositorio coordinador de ambos servicios
- ViewModel con gestión de estado reactivo con StateFlow
- UI moderna con Jetpack Compose y Material Design 3
- Sistema de umbrales configurables para detección de eventos
- Indicadores visuales con colores por severidad
- Animaciones y transiciones suaves
- Documentación completa en README.md
- Permisos para ubicación, notificaciones y servicios en primer plano

### Changed

- 

### Removed

- 

### Fixed

- 

---

## Notas de Desarrollo

Este proyecto implementa una solución innovadora que integra:
- **GPS**: Para ubicación, velocidad y seguimiento de ruta
- **Acelerómetro**: Para detectar movimientos bruscos y patrones de conducción

La aplicación procesa todos los datos localmente, garantizando la privacidad del usuario.

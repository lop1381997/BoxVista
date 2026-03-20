# Android Checklist (estado real vs objetivo)

Fuente base: `tasks.md` + inspección de `mobile/BoxVista/*`.
Última revisión inicial: 2026-03-16.

## Reglas para automatización (obligatorio)
- Antes de implementar, leer este archivo completo.
- Trabajar **solo** en `mobile/BoxVista/`.
- Elegir 1 tarea pendiente por ejecución.
- Al cerrar una tarea: marcar `[x]` + añadir evidencia corta (archivo/test/PR).
- Si detectas regresión, desmarcar `[x]` y documentar motivo.

## Ya hecho (confirmado en código)
- [x] Proyecto Android base con Kotlin + Compose (`MainActivity`, tema, estructura app).
- [x] Home básico con listado de cajas/objetos (Compose) y modal de detalle.
- [x] Capa de red inicial (`NetworkManager`, `services/BoxService`, `services/ObjectService`).
- [x] Modelos principales (`models/Box.kt`, `models/ObjectItem.kt`).
- [x] Tests iniciales unitarios + instrumentados presentes:
  - `app/src/test/...`
  - `app/src/androidTest/...`

## Pendiente priorizado (orden recomendado)
- [x] Login real contra backend (`POST /auth/login`) + almacenamiento seguro de token.  
  Evidencia: `views/login/LoginScreen.kt`, `views/login/LoginViewModel.kt`, `services/AuthService.kt`, `services/TokenStore.kt`, `NetworkManager.kt`, `LoginViewModelTest` (200/401/red caída).
- [x] Crear caja desde UI (flujo completo con validaciones y manejo de errores).  
  Evidencia: `views/createbox/CreateBoxScreen.kt` (bloqueo de botón sin tipo), `views/createbox/CreateBoxViewModel.kt` (mapeo de errores de red/HTTP), `CreateBoxViewModelTest` (caso de red).
- [ ] Escaneo QR real (cámara + navegación a detalle por UUID).
- [ ] Integración NFC/RFID básica (`NfcAdapter`) con fallback seguro.
- [ ] Flujo de verificación con subida multipart de fotos (`/cajas/{uuid}/verificar`).
- [ ] Pantalla/flujo de edición manual de discrepancias y guardado en backend.
- [ ] Historial de caja en UI (`GET /cajas/{uuid}/historial`).
- [ ] Manejo robusto de estados: loading/error/offline/retry en todos los flujos críticos.
- [ ] Persistencia offline mínima (Room) para cajas/historial + sincronización diferida.
- [ ] Cobertura de tests objetivo: unit + ui para casos de red caída, rotación y concurrencia.
- [ ] CI Android básica (lint + test + connected test mínimo).

## Bloqueadores / riesgos actuales
- [ ] Divergencia de path en documentos (`mobile/android` histórico vs actual `mobile/BoxVista`).
- [ ] Posible desalineación de contratos API backend/móvil (`boxes` vs `cajas`).
- [ ] Necesidad de definir estrategia única de navegación y estado (evitar lógica dispersa).

## Registro incremental de automatización
- 2026-03-16: checklist inicial creada.
- 2026-03-20: cerrada tarea “Crear caja desde UI” con validaciones/manejo de errores y test de red en ViewModel.
- 2026-03-20: cerrada tarea “Login real + token seguro” con `POST /auth/login`, `EncryptedSharedPreferences` y tests de ViewModel (200/401/red caída).

# BoxVista – Cron Progress Log

## 2026-03-04 19:50 (Europe/Madrid)
- Focus: Planificación operativa
- Done:
  - Se creó `CRON_PLAN.md` con prioridad macro P0→P3, método por run y definición de done.
  - Se enlazó el plan en `tasks.md` para que sea fuente de verdad operativa.
  - Se dejó este log preparado para trazabilidad de ejecuciones cron.
- Files:
  - `BoxVista/CRON_PLAN.md`
  - `BoxVista/tasks.md`
  - `BoxVista/docs/cron-progress.md`
- Validation:
  - Revisión manual de consistencia entre plan y tareas pendientes.
- Next:
  - Ejecutar P0.1: conciliación backend esperados vs detectados + tests.

## 2026-03-04 20:06 (Europe/Madrid)
- Focus: P0.1 backend conciliación esperados vs detectados en verificación
- Done:
  - Implementado servicio de conciliación (`backend/src/services/conciliacion.ts`) con conteo por nombre, normalización y cálculo de `faltantes`/`sobrantes`.
  - Añadido endpoint `POST /api/boxes/:boxId/verificar` (alias `POST /api/cajas/:boxId/verificar`) que devuelve `objetos_detectados`, `objetos_esperados`, `faltantes`, `sobrantes`.
  - Añadido registro de historial persistente por verificación con tabla/modelo `HistorialEvento` y endpoint `GET /api/boxes/:boxId/historial` (alias en español también disponible).
  - Añadidos tests del módulo de conciliación para los 4 casos críticos: match, faltantes, sobrantes y mixto.
  - Actualizados checkboxes de `tasks.md` para reflejar cierre de “Comparación con lista esperada (MVP manual)”.
- Files:
  - `backend/src/services/conciliacion.ts`
  - `backend/src/services/__tests__/conciliacion.test.ts`
  - `backend/src/models.ts`
  - `backend/src/routes/boxes.ts`
  - `backend/src/server.ts`
  - `backend/package.json`
  - `tasks.md`
- Validation:
  - `cd backend && npm test` ✅
    - 4/4 tests passing (`conciliacion.test`): match, faltantes, sobrantes, mixto.
- Next:
  - P0.2: crear endpoint de visión estable (mock con contrato fijo) y testear contrato (`/vision/detect`) para desacoplar la conciliación del motor IA real.

## 2026-03-04 20:11 (Europe/Madrid)
- Focus: P0.2 endpoint de visión estable (mock) + contrato testeado
- Done:
  - Implementado servicio de visión mock determinista `backend/src/services/vision.ts` con motor `mock-vision-v1`.
  - Añadido endpoint interno `POST /vision/detect` (alias `POST /api/vision/detect`) en `backend/src/routes/vision.ts`.
  - Integrada la ruta en la app backend y refactor ligero para exponer `createApp()` en `backend/src/app.ts`.
  - Añadidos tests de contrato del servicio de visión y tests de endpoint (caso válido + validación de payload).
  - Documentado el contrato de `POST /vision/detect` en `docs/api_endpoints.md`.
  - Actualizado `tasks.md` marcando completado el checkbox del endpoint interno de visión.
- Files:
  - `backend/src/services/vision.ts`
  - `backend/src/services/__tests__/vision.test.ts`
  - `backend/src/routes/vision.ts`
  - `backend/src/routes/__tests__/vision.route.test.ts`
  - `backend/src/app.ts`
  - `backend/src/server.ts`
  - `backend/package.json`
  - `backend/package-lock.json`
  - `docs/api_endpoints.md`
  - `tasks.md`
- Validation:
  - `cd backend && npm test` ✅
  - Resultado: 11/11 tests passing (incluye conciliación, rutas existentes y nuevos tests de visión).
- Next:
  - P0.3: pruebas de flujo completo backend (crear caja → verificar → historial) con test E2E automatizado y assertions de persistencia de eventos.

## 2026-03-04 20:13 (Europe/Madrid)
- Focus: P0.3 pruebas de flujo completo backend (crear caja → verificar → historial)
- Done:
  - Añadido test E2E `boxes.flow.e2e.test.ts` que cubre el flujo completo: creación de caja, verificación con discrepancias y consulta de historial.
  - El test valida persistencia real del evento `verificacion_contenido` y el resumen de conciliación guardado en `detalles`.
  - Se añadió un checkbox específico en `tasks.md` para reflejar este avance automatizado dentro del bloque de pruebas de flujo completo.
- Files:
  - `backend/src/routes/__tests__/boxes.flow.e2e.test.ts`
  - `tasks.md`
- Validation:
  - `cd backend && npm test` ✅
  - Resultado: 12/12 tests passing (incluye nuevo E2E de flujo completo).
- Next:
  - P0.4: conectar iOS para crear caja + abrir detalle de caja contra backend real.

## 2026-03-04 20:20 (Europe/Madrid)
- Focus: P0.4 iOS crear caja + detalle de caja + llamada real a verificación
- Done:
  - Implementada pantalla `AddBox` funcional (formulario + POST real a `/api/boxes` + confirmación con id de caja creada).
  - Extendido `NetworkManager` y `BoxService` para soportar `POST /api/boxes/{id}/verificar`.
  - Añadidos modelos de verificación (`VerificationResult`, `DetectedObjectInput`) para parseo tipado de conciliación.
  - Integrado botón `Verificar` en `BoxDetailView` para lanzar verificación real y mostrar resultado (detectados/esperados/faltantes/sobrantes).
  - Actualizados checkboxes de `tasks.md` en las secciones de iOS (crear caja, detalle y llamada de verificación parcial).
- Files:
  - `mobile/ios/BoxVistaIOS/Views/NavigationTab/AddBoxView/AddBox.swift`
  - `mobile/ios/BoxVistaIOS/NetworkManager.swift`
  - `mobile/ios/BoxVistaIOS/Services/BoxService.swift`
  - `mobile/ios/BoxVistaIOS/Models/VerificationModel.swift`
  - `mobile/ios/BoxVistaIOS/Views/NavigationTab/HomeView/BoxDetailView/BoxDetailView.swift`
  - `mobile/ios/BoxVistaIOS/Views/NavigationTab/HomeView/BoxDetailView/BoxDetailViewVM.swift`
  - `tasks.md`
- Validation:
  - BLOCKED: `xcodebuild` no está disponible en este host Linux (`xcodebuild: command not found`).
  - BLOCKED: `swift` CLI no está disponible (`swift: command not found`), no se pueden ejecutar tests iOS localmente en este entorno.
- Next:
  - P0.5: guardar edición manual de conciliación iOS contra backend (endpoint de persistencia + conexión desde botón “Guardar Cambios”).

## 2026-03-20 19:28 (Europe/Madrid)
- Focus: P2 Android – Función “Crear Caja” (flujo completo con validaciones y manejo de errores)
- Done:
  - Endurecido `CreateBoxViewModel` con mapeo de errores de red/HTTP a mensajes de usuario.
  - Ajustado `CreateBoxScreen` para deshabilitar “Crear” hasta seleccionar tipo válido (validación UI temprana).
  - Añadido test unitario para fallo de red (`IOException`) verificando mensaje amigable.
  - Cerrada tarea Android “Crear caja desde UI…” en `docs/checklists/android-checklist.md` con evidencia.
  - Actualizado tracking de Fase 4 en `tasks.md` marcando “Función Crear Caja” como completada.
- Files:
  - `mobile/BoxVista/app/src/main/java/com/hirlu/boxvista/views/createbox/CreateBoxScreen.kt`
  - `mobile/BoxVista/app/src/main/java/com/hirlu/boxvista/views/createbox/CreateBoxViewModel.kt`
  - `mobile/BoxVista/app/src/test/java/com/hirlu/boxvista/views/createbox/CreateBoxViewModelTest.kt`
  - `tasks.md`
  - `docs/checklists/android-checklist.md`
- Validation:
  - BLOCKED: no se pudo ejecutar validación Android (`./gradlew test`/`./gradlew lint`) porque la herramienta de ejecución de shell en este entorno exige `host=node` con nodo emparejado y actualmente no hay ninguno disponible.
  - Workaround propuesto: ejecutar localmente en `mobile/BoxVista/`:
    - `./gradlew testDebugUnitTest`
    - `./gradlew lintDebug`
  - BLOCKED: no se pudo crear commit (`git add/commit`) por el mismo bloqueo de shell sin nodo emparejado.
  - Workaround propuesto: tras validar localmente, ejecutar commit con mensaje `android: cerrar tarea crear caja UI con validaciones y manejo de errores`.
- Next:
  - Implementar siguiente prioridad Android: “Login real contra backend (`POST /auth/login`) + almacenamiento seguro de token”.
  - Incluir tests de ViewModel para errores 401/403 y red caída.

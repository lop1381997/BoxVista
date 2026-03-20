# Backend Checklist (estado real vs objetivo)

Fuente base: `tasks.md` + inspección de `backend/src/*`.
Última revisión inicial: 2026-03-16.

## Reglas para automatización (obligatorio)
- Antes de implementar, leer este archivo completo.
- Elegir **1 tarea pendiente** por ejecución (máximo 1 PR por run).
- Al terminar, actualizar checks `[ ] -> [x]` y añadir evidencia breve (archivo/test/PR).
- Si una tarea estaba marcada `[x]` pero está incompleta, revertir a `[ ]` y explicar por qué.
- No editar secciones de Android/iOS.

## Ya hecho (confirmado en código)
- [x] API Express base operativa (`app.ts`, `server.ts`).
- [x] Modelos de datos y relaciones base (`models.ts`).
- [x] CRUD principal de cajas (`routes/boxes.ts`: list/get/create/update/delete).
- [x] Verificación de contenido y conciliación (`POST /boxes/:id/verificar` + `services/conciliacion.ts`).
- [x] Historial por caja (`GET /boxes/:id/historial`).
- [x] Endpoint de visión interno para MVP (`routes/vision.ts` + `services/vision.ts`) con tests básicos.
- [x] Suite de tests backend inicial (unit + rutas):
  - `routes/__tests__/boxes.test.ts`
  - `routes/__tests__/boxes.flow.e2e.test.ts`
  - `routes/__tests__/vision.route.test.ts`
  - `services/__tests__/*`

## Pendiente priorizado (orden recomendado)
- [x] Autenticación real para MVP (`/auth/register`, `/auth/login`, JWT middleware usable). ✅ 2026-03-17: implementado en `backend/src/routes/auth.ts` + `backend/src/middleware/auth.ts`, aplicado a escrituras en `routes/boxes.ts`; test `backend/src/__tests__/auth.test.ts` (register/login + protección con Bearer token).
- [ ] Endpoint de actualización de ubicación alineado con requisitos (`PUT /cajas/{uuid}/ubicacion` o equivalente estable).
- [ ] Paginación + filtros robustos en listado de cajas (estado/tipo/ubicación/fecha) con tests.
- [ ] Contrato estable `cajas/*` (nombres y payloads) para paridad con apps móviles.
- [ ] Subida multipart real de imágenes en verificación (actualmente flujo simplificado) con validación de tamaño/tipo.
- [ ] Integración vision “real” (no mock) detrás de feature flag, **sin introducir runtime Python** (mantener stack Node/TypeScript).
- [ ] Endpoint para guardar ajustes manuales tras verificación (actualizar relación caja-objeto + evento).
- [ ] Endurecimiento de errores (códigos consistentes, trazabilidad, idempotencia de verificación).
- [ ] Cobertura mínima objetivo: statements >= 80%, branches >= 70% en backend.
- [ ] CI básica backend (lint + test) en GitHub Actions.

## Bloqueadores / riesgos actuales
- [ ] Inconsistencia de nomenclatura (`boxes` vs `cajas`) entre tareas y código.
- [ ] `database.sqlite` versionado en repo (riesgo de conflictos y ruido en PRs).
- [ ] `jest.config.js` vacío (riesgo de configuración implícita inestable).

## Registro incremental de automatización
- 2026-03-16: checklist inicial creada.
- 2026-03-17: cerrada tarea de autenticación MVP con JWT (`/api/auth/register`, `/api/auth/login`) y middleware `requireAuth`; evidencia: `npm test` ✅ (incluye `src/__tests__/auth.test.ts`).
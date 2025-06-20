## BoxVista – Lista de Tareas

### Fase 1: Preparación y diseño general

- [x] Definir requisitos funcionales y no funcionales
  - [x] Enumerar casos de uso mínimos (MVP): escaneo RFID/QR, toma de fotos, subida al backend, validación manual de lista.
  - [x] Decidir tecnologías principales (lenguajes, frameworks, base de datos, servicio de IA).
- [x] Diseñar esquema de base de datos
  - [x] Crear diagrama entidad-relación (ER) con tablas:
    - [x] **Cajas** (UUID, tipo, fecha_creación, ubicación, estado).
    - [x] **Objetos** (ID, nombre, descripción, categoría, peso, dimensiones, etiquetas opcionales).
    - [x] **Caja-Objeto** (UUID_caja, ID_objeto, cantidad, estado, timestamp_de_verificación).
    - [x] **HistorialEventos** (UUID_evento, UUID_caja, tipo_evento, usuario, timestamp, detalles).
  - [x] Especificar tipos de campo y llaves primarias/foráneas.
- [x] Configurar repositorios y entorno de desarrollo
  - [x] Crear repositorio Git (GitHub/GitLab) con carpetas `backend/` y `mobile/`.
  - [x] Configurar entorno virtual para backend (Python + virtualenv o Node.js + nvm).
  - [x] Crear proyecto inicial de iOS (Xcode) y proyecto inicial de Android (Android Studio).
- [x] Definir endpoints y contratos de la API
  - [x] Listar rutas básicas:
    - [x] POST `/cajas`
    - [x] GET `/cajas/{uuid}`
    - [x] POST `/cajas/{uuid}/verificar`
    - [x] GET `/cajas/{uuid}/objetos-esperados`
    - [x] PUT `/cajas/{uuid}/ubicacion`
    - [x] GET `/cajas`
    - [x] POST `/auth/login`
    - [x] POST `/auth/register`
  - [x] Definir JSON de request/response mínimo para cada endpoint.

### Fase 2: Implementación del backend MVP

- [x] Inicializar proyecto back-end
  - [x] Elegir framework (FastAPI en Python o Express.js en Node).
  - [x] Crear estructura de carpetas:
    ```
    backend/
      ├─ app/
      │   ├─ main.py
      │   ├─ routers/
      │   ├─ models/
      │   ├─ schemas/
      │   ├─ services/
      │   └─ utils/
      ├─ requirements.txt o package.json
      └─ .env
    ```
- [x] Configurar conexión a base de datos
  - [x] Instalar ORM (SQLAlchemy o Sequelize/TypeORM).
  - [x] Crear configuración en `.env` para cadena de conexión.
  - [x] Codificar modelos de datos según el esquema ER.
  - [x] Probar migraciones iniciales o creación de tablas.
- [x] Implementar sistema de autenticación básico
  - [x] Crear modelo de usuario (ID, correo, contraseña hashed, rol).
  - [x] Endpoint de registro y login (hash de contraseña, JWT o token simple).
  - [x] Middleware para proteger rutas según rol.
- [x] Crear endpoints CRUD de cajas
  - [x] POST `/cajas`
    - [x] Generar UUID automáticamente.
    - [x] Recibir tipo de caja y lista de objetos inicial (opcional).
    - [x] Insertar en tabla Cajas.
    - [x] Devolver UUID y datos.
  - [x] GET `/cajas/{uuid}`
    - [x] Recuperar información de la caja (metadatos + últimos objetos detectados + estado).
    - [x] Devolver JSON.
  - [x] PUT `/cajas/{uuid}/ubicacion`
    - [x] Recibir nueva ubicación (texto).
    - [x] Actualizar registro en BD y crear evento en historial.
  - [x] GET `/cajas`
    - [x] Parámetros opcionales: estado, tipo, ubicación, fecha_última_verificación.
    - [x] Devolver listado paginado.
- [x] Implementar subida y almacenamiento de imágenes
  - [x] Decidir estrategia de almacenamiento (local `/uploads/` o bucket S3).
  - [x] Endpoint POST `/cajas/{uuid}/verificar`: recibir imágenes (multipart/form-data) y almacenar.
  - [x] Validar tipos de archivo y tamaño máximo.
- [ ] Integrar motor de IA de detección de objetos
  - [x] Crear submódulo `services/vision.py`.

  - [ ] Empezar con modelo genérico (YOLOv5 preentrenado).
  - [ ] Instalar dependencias (PyTorch, OpenCV).
  - [ ] Probar inferencia local con imágenes de ejemplo.
  - [ ] Crear endpoint interno `/vision/detect` para recibir rutas de imágenes y devolver lista de objetos detectados.
- [ ] Comparación con lista esperada (MVP manual)
  - [ ] En tabla **Caja-Objeto**, permitir ingreso manual de lista esperada al crear caja.
  - [ ] Implementar `services/conciliacion.py` para comparar lista esperada vs. lista detectada.
  - [ ] Endpoint `/cajas/{uuid}/verificar` devuelve:
    - [ ] `objetos_detectados`
    - [ ] `objetos_esperados`
    - [ ] `faltantes`
    - [ ] `sobrantes`
- [x] Guardar registro en historial
  - [x] Cada llamada a `/cajas/{uuid}/verificar` crea un registro en **HistorialEventos** con:
    - [x] `tipo_evento = “verificacion_contenido”`
    - [x] `detalles` (listas de faltantes/sobrantes, usuario).
  - [x] Endpoint GET `/cajas/{uuid}/historial`.
- [ ] Probar flujo completo vía Postman/Insomnia
  - [ ] Crear caja.
  - [ ] Definir manualmente lista esperada.
  - [ ] Subir fotos, llamar a IA, recibir diferencias.
  - [ ] Verificar registro de eventos y estado de la caja.

### Fase 3: MVP iOS (Meses 1–4)

- [x] Configurar proyecto Xcode
  - [x] Crear nuevo proyecto en Swift (Single View App o SwiftUI).
  - [x] Configurar Podfile o Swift Package Manager:

- [x] Asistente de Captura de Fotos
  - [x] Pantalla con instrucción breve para tomar fotos.
  - [x] Botón para abrir cámara y tomar 1–3 fotos (`UIImagePickerController` o API de cámara).
  - [x] Guardar fotos localmente en carpeta temporal.
  - [x] Mostrar previsualización de fotos y botón “Subir y Verificar”.
- [x] Pantalla para edición manual de lista
  - [x] Listar `objetos_detectados` y `objetos_esperados`.
  - [x] Permitir:
    - [x] Marcar objetos detectados como “válidos”.
    - [x] Añadir objetos faltantes (campo de texto + cantidad).
    - [x] Eliminar objetos sobrantes.
- [ ] Función “Crear Caja”
  - [ ] Pantalla para elegir “Tipo de caja” (picker con valores hardcodeados).
  - [ ] Botón “Crear” llama a POST `/cajas`, recibe UUID.
  - [ ] Mostrar pantalla de confirmación con UUID y opción de “Imprimir etiqueta” (mostrar UUID para QR).
- [ ] Escaneo de RFID/QR
  - **QR**
    - [ ] Usar `AVCaptureSession` para detectar QR en cámara.
    - [ ] Al leer QR, extraer UUID y navegar a detalle de caja.
  - **RFID/NFC**
    - [ ] Usar `CoreNFC` para leer tags compatibles.
    - [ ] Extraer UUID y redirigir a detalle de caja.
- [ ] Pantalla de Detalle de Caja
  - [ ] Mostrar metadatos: UUID, tipo, ubicación, estado.
  - [ ] Lista estática de “objetos esperados” (inicialmente vacía).
  - [ ] Botón “Verificar Contenido” para iniciar flujo de captura de fotos.
- [x] Asistente de Captura de Fotos
  - [x] Pantalla con instrucción breve para tomar fotos.
  - [x] Botón para abrir cámara y tomar 1–3 fotos (`UIImagePickerController` o API de cámara).
  - [x] Guardar fotos localmente en carpeta temporal.
  - [x] Mostrar previsualización de fotos y botón “Subir y Verificar”.

- [ ] Llamada a backend para verificación
  - [ ] Al tocar “Subir y Verificar”:
    - [ ] Empaquetar imágenes en multipart/form-data junto con token JWT.
    - [ ] Llamar a POST `/cajas/{uuid}/verificar`.
    - [ ] Procesar respuesta JSON: objetos detectados, faltantes, sobrantes.
  - [ ] Si hay faltantes o sobrantes:
    - [ ] Mostrar alerta con detalles.
    - [ ] Opciones: “Confirmar manualmente” o “Marcar como verificado de todas formas”.
- [x] Pantalla para edición manual de lista
  - [x] Listar `objetos_detectados` y `objetos_esperados`.
  - [x] Permitir:
    - [x] Marcar objetos detectados como “válidos”.
    - [x] Añadir objetos faltantes (campo de texto + cantidad).
    - [x] Eliminar objetos sobrantes.

  - [ ] Botón “Guardar Cambios” llama a endpoint para actualizar relación Caja-Objeto y crea evento en historial.
- [ ] Historial de Eventos en la interfaz
  - [ ] Botón “Ver Historial” en detalle de caja.
  - [ ] Llamar GET `/cajas/{uuid}/historial`.
  - [ ] Mostrar listado cronológico: fecha, usuario, descripción breve.
- [ ] Sistema de notificaciones push
  - [ ] Configurar Firebase Cloud Messaging (FCM) para iOS.
  - [ ] Registrar dispositivo en backend con token.
  - [ ] Definir notificaciones para:
    - [ ] Discrepancias en caja (faltantes/sobrantes).
    - [ ] Recordatorio de verificación tras X meses.
  - [ ] Mostrar alerta local cuando llegue notificación.
- [ ] Modo offline / caché simple
  - [ ] Crear capa de persistencia local (SQLite o Core Data).
  - [ ] Al llamar a endpoints (cajas, historial), guardar respuesta local.
  - [ ] Si no hay conexión, mostrar datos desde la cache.
  - [ ] En verificación offline, almacenar fotos y datos en cola local y sincronizar luego.
- [ ] Pruebas unitarias básicas
  - [ ] Usar XCTest para:
    - [ ] Parseo de JSON de API.
    - [ ] Autenticación/login.
    - [ ] Lógica local de conciliación (datos de prueba).
- [ ] Configuración de CI/CD (opcional)
  - [ ] Configurar GitHub Actions para:
    - [ ] Ejecutar lint de Swift (SwiftLint).
    - [ ] Ejecutar tests unitarios.
    - [ ] Validar que build no falle.

### Fase 4: MVP Android (Meses 5–8)

- [ ] Configurar proyecto Android
  - [ ] Crear proyecto en Android Studio con Kotlin.
  - [ ] Añadir dependencias en `build.gradle`:
    - [ ] Lector QR (ZXing o ML Kit).
    - [ ] Cliente HTTP (Retrofit + OkHttp).
    - [ ] JSON serialization (Gson o Moshi).
    - [ ] Soporte NFC para RFID (si el tag es compatible).
- [ ] Implementar pantalla de login
  - [ ] Actividad/Fragment con campos “correo” y “contraseña”.
  - [ ] Con Retrofit, llamar POST `/auth/login`.
  - [ ] Guardar token JWT en `SharedPreferences`.
  - [ ] Manejar errores de red y credenciales.
- [ ] Pantalla principal (Dashboard)
  - [ ] RecyclerView para listar cajas (GET `/cajas`).
  - [ ] Mostrar UUID parcial, estado con color, fecha última verificación.
  - [ ] Botón flotante “Crear nueva caja”.
- [ ] Función “Crear Caja”
  - [ ] Actividad para elegir “Tipo de caja” (Spinner).
  - [ ] Al tocar “Crear”, llamar POST `/cajas` y recibir UUID.
  - [ ] Mostrar diálogo con UUID y opción “Mostrar QR”.
- [ ] Escaneo de RFID/QR
  - **QR**
    - [ ] Integrar cámara y ZXing para detectar código.
    - [ ] Extraer UUID y navegar a actividad de detalle.
  - **RFID/NFC**
    - [ ] Configurar `NfcAdapter` para escuchar tags.
    - [ ] Al leer, obtener UUID y abrir detalle de caja.
- [ ] Pantalla de Detalle de Caja
  - [ ] Mostrar metadatos: UUID, tipo, ubicación, estado.
  - [ ] Mostrar lista estática de “objetos esperados” (vacía inicialmente).
  - [ ] Botón “Verificar Contenido” para iniciar flujo de fotos.
- [ ] Asistente de Captura de Fotos
  - [ ] Activity/Fragment para tomar fotos (CameraX o Intent de cámara).
  - [ ] Guardar imágenes en almacenamiento interno temporal.
  - [ ] Vista previa de miniaturas y botón “Subir y Verificar”.
- [ ] Subida y verificación
  - [ ] Con Retrofit y `MultipartBody.Part`, enviar fotos a POST `/cajas/{uuid}/verificar`.
  - [ ] Procesar JSON: `objetos_detectados`, `faltantes`, `sobrantes`.
  - [ ] Si hay discrepancias, mostrar diálogo y opciones “Corregir manual” o “Marcar verificado”.
- [ ] Edición manual de lista
  - [ ] Activity/Fragment para editar lista de objetos.
  - [ ] Mostrar listas: detectados vs. esperados.
  - [ ] Permitir añadir/eliminar/ajustar cantidades.
  - [ ] Al confirmar, llamar endpoint para actualizar relación Caja-Objeto y crear evento.
- [ ] Historial de eventos
  - [ ] Activity con RecyclerView para GET `/cajas/{uuid}/historial`.
  - [ ] Mostrar fecha, usuario, descripción breve.
- [ ] Notificaciones push en Android
  - [ ] Configurar Firebase Cloud Messaging (FCM).
  - [ ] Registrar token en backend.
  - [ ] Configurar recepción de notificaciones y alerta local.
- [ ] Modo offline / caché simple
  - [ ] Implementar Room o SQLite para persistir datos (cajas, historial).
  - [ ] Si no hay conexión, mostrar datos desde la base local.
  - [ ] En verificación offline, guardar fotos y datos en cola y sincronizar luego.
- [ ] Pruebas unitarias básicas
  - [ ] Usar JUnit y Mockito para:
    - [ ] Verificar parseo de JSON.
    - [ ] Lógica local de conciliación (listas de prueba).
    - [ ] Tests de ViewModel o utilidades.
- [ ] Configuración de CI/CD (opcional)
  - [ ] GitHub Actions para:
    - [ ] Ejecutar lint de Kotlin (Detekt/KtLint).
    - [ ] Ejecutar tests unitarios.
    - [ ] Validar que build no falle.

### Fase 5: Versión 1.0 (Apps iOS & Android, Meses 9–16)

- [ ] Integración con ERP/WMS (simulación MVP)
  - [ ] Definir endpoint `/cajas/{uuid}/lista-esperada` que devuelva datos estáticos.
  - [ ] En iOS/Android, reemplazar ingreso manual de lista esperada por obtención automática de dicho endpoint.
- [ ] Mejorar el modelo de IA y servicio de visión
  - [ ] Reentrenar modelo con imágenes reales recolectadas durante pruebas MVP.
  - [ ] Ajustar precisión (etiquetar nuevas imágenes, crear dataset propio, volver a entrenar YOLOv5/TensorFlow).
  - [ ] Desplegar modelo actualizado en backend y probar con nuevas fotos.
- [ ] Implementar roles y permisos
  - [ ] Backend: añadir campo `rol` en tabla Usuarios (Operador, Supervisor, Administrador).
  - [ ] Proteger endpoints según rol:
    - [ ] Solo Administrador: crear/modificar tipo de caja.
    - [ ] Supervisor: editar lista de objetos.
    - [ ] Operador: verificar y consultar datos.
  - [ ] iOS/Android: al iniciar sesión, ocultar/desactivar botones según rol.
- [ ] Modo Offline Avanzado
  - [ ] Mejorar persistencia local y sincronización bidireccional.
  - [ ] Gestionar conflictos de edición offline/online con diálogo de resolución.
  - [ ] Probar en entorno sin cobertura: creación de caja, verificación y sincronización posterior.
- [ ] Dashboard Analítico
  - [ ] Backend: crear endpoints:
    - [ ] GET `/reportes/discrepancias?fecha_inicio=&fecha_fin=`
    - [ ] GET `/reportes/inventario-activo`
  - [ ] Web o vista en app para mostrar gráficos básicos (Chart.js en web o librería nativa en móvil):
    - [ ] Número de discrepancias por tipo de caja.
    - [ ] Inventario actual (totales de objetos por categoría).
  - [ ] (Opcional) Pequeña versión web con React para dashboards.
- [ ] Implementar ledger inmutable (blockchain-like)
  - [ ] En backend, cada evento:
    - [ ] Calcular `hash_evento = SHA256(hash_evento_anterior + datos_evento + timestamp)`.
    - [ ] Almacenar `hash_evento` en tabla HistorialEventos.
  - [ ] Endpoint GET `/cajas/{uuid}/verificar-integridad` que recorra hashes y confirme cadena intacta.
- [ ] Notificaciones y recordatorios configurables
  - [ ] En backend, cron job o scheduler que:
    - [ ] Cada día revisa cajas con última verificación hace más de X meses.
    - [ ] Envía notificación push o email al Operador responsable.
  - [ ] En iOS/Android, permitir configurar:
    - [ ] Intervalo de re-verificación (en días).
    - [ ] Tipo de alerta (push, email o ambas).
- [ ] Pruebas integrales y corrección de bugs
  - [ ] Pruebas extremo a extremo (crear caja, verificar, conflictos offline, cambios de rol, etc.).
  - [ ] Uso de testers externos para simular en almacenes.
  - [ ] Corregir errores críticos y mejorar UX/UI según feedback.
- [ ] Preparar despliegue a producción
  - **iOS**:
    - [ ] Configurar perfiles de distribución, iconos e imágenes de lanzamiento.
    - [ ] Generar build para TestFlight y luego App Store.
  - **Android**:
    - [ ] Configurar clave de firma, iconos y pantallas de bienvenida.
    - [ ] Generar APK/AAB y subir a Google Play Console (Canal interno primero).
  - **Backend**:
    - [ ] Desplegar en servidor (DigitalOcean, AWS EC2, etc.) con HTTPS (SSL).
    - [ ] Configurar variables de entorno y base de datos en producción.

### Fase 6: Versión 2.0 y más allá (Meses 17–30)

- [ ] Módulo IoT: temperatura y humedad
  - [ ] Seleccionar sensores BLE (por ejemplo, DHT22 con microcontrolador BLE).
  - [ ] Crear servicio en backend para recibir datos de sensores (REST o MQTT).
  - [ ] iOS/Android: implementar lector/suscriptor BLE para cada sensor asociado a caja.
  - [ ] Almacenar lecturas en tabla `SensoresLecturas` (UUID_caja, timestamp, temperatura, humedad).
  - [ ] En app, mostrar gráficas sencillas de histórico de temperatura/humedad por caja.
- [ ] Realidad Aumentada (AR) para verificación
  - [ ] iOS: investigar ARKit.
  - [ ] Android: investigar ARCore.
  - [ ] Diseñar prototipo de AR: al apuntar al interior de la caja, dibujar marcas sobre objetos detectados en tiempo real.
  - [ ] Implementar MVP de AR en iOS o Android según prioridad.
- [ ] Reconocimiento de voz
  - [ ] iOS: integrar `Speech` framework.
  - [ ] Android: usar `SpeechRecognizer`.
  - [ ] Permitir modo “manos libres”: dictar “Agregar 2 tornillos, 1 manual, 3 cables” y convertir texto en lista.
  - [ ] Confirmar antes de modificar la lista.
- [ ] Gamificación de operarios
  - [ ] Definir esquema de puntos:
    - [ ] +10 puntos por verificación sin discrepancias.
    - [ ] +20 puntos por detectar y reportar objeto defectuoso.
  - [ ] Backend: agregar tabla `Puntuaciones` (usuario, puntos, evento).
  - [ ] App: sección “Mi puntuación” con ranking interno (top 5 operadores).
  - [ ] Crear badges (iconos) para hitos (100 puntos → “Inspector Experto”, 500 puntos → “Maestro de Inventario”).
- [ ] Optimización de layout de bodegas
  - [ ] Analizar histórico en backend: frecuencia de apertura/verificación y cantidad de movimientos por caja.
  - [ ] Algoritmo sugerencia:
    - [ ] Cajas de alto movimiento al frente.
    - [ ] Consolidar cajas con objetos relacionados.
  - [ ] Mostrar recomendaciones en dashboard web y sección “Sugerencias de Ubicación” en apps.
- [ ] Soporte multilingüe y regionalización
  - [ ] Extraer textos fijos a archivos de localización (`.strings` en iOS, `strings.xml` en Android).
  - [ ] Traducir a inglés y portugués.
  - [ ] Verificar formatos de fecha/moneda según región.
  - [ ] Validar normativas locales en etiquetas (p ej. formato de peso en kg para España).
- [ ] Marketing y adopción
  - [ ] Crear página web landing simple para BoxVista:
    - [ ] Características clave.
    - [ ] Capturas de pantalla de las apps.
    - [ ] Formulario para solicitar demo.
  - [ ] Preparar material de venta (presentación PPT) para clientes potenciales:
    - [ ] Resaltar ROI, casos de uso y beneficios.
  - [ ] Contactar clientes piloto (empresas de logística, laboratorios, etc.) para pruebas de campo.

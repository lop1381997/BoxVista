# Requisitos Funcionales y No Funcionales

## Requisitos Funcionales (MVP)
1. **Escaneo de RFID/QR:** la aplicación móvil debe ser capaz de leer etiquetas RFID y códigos QR para identificar cada caja por su UUID.
2. **Captura de fotos:** el usuario debe poder tomar de una a tres fotos del contenido de la caja para su análisis.
3. **Subida de imágenes al backend:** las fotos tomadas deben enviarse al servidor para procesarlas mediante IA.
4. **Validación manual de lista:** el usuario podrá confirmar o ajustar manualmente la lista de objetos detectados.
*Por el momento, la detección automática mediante IA queda pendiente para futuras versiones.*

## Requisitos No Funcionales
1. **Seguridad:** todas las comunicaciones deben realizarse sobre HTTPS; las credenciales y tokens deben almacenarse de forma segura.
2. **Rendimiento:** la respuesta del backend para operaciones comunes (creación de caja, verificación) debe ser inferior a 500 ms en condiciones normales.
3. **Escalabilidad:** la arquitectura debe permitir añadir nuevas funcionalidades (por ejemplo, módulos IoT o analíticas) sin grandes refactorizaciones.
4. **Disponibilidad offline:** la app móvil debe funcionar sin conexión, almacenando los datos localmente para sincronizarlos posteriormente.

## Tecnologías Elegidas
- **Backend:** Node.js + TypeScript utilizando el framework **Fastify** por su velocidad y soporte de TypeScript.
- **Base de datos:** **PostgreSQL** gestionada mediante **TypeORM** como ORM principal.
- **Servicio de IA:** microservicio en Python con **YOLOv5** para detección de objetos, expuesto mediante un endpoint REST.
- **Apps móviles:**
  - iOS en **Swift**.
  - Android en **Kotlin**.

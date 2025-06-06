# Endpoints Básicos de la API

## Autenticación
- **POST `/auth/login`**
  - Request: `{ "email": "string", "password": "string" }`
  - Response: `{ "token": "jwt" }`
- **POST `/auth/register`**
  - Request: `{ "email": "string", "password": "string" }`
  - Response: `{ "id": number, "email": "string" }`

## Cajas
- **POST `/cajas`**
  - Request: `{ "tipo": "string", "objetos": [{"id_objeto": number, "cantidad": number}] }`
  - Response: `{ "uuid": "uuid", "tipo": "string" }`
- **GET `/cajas/{uuid}`**
  - Response: `{ "uuid": "uuid", "tipo": "string", "ubicacion": "string", "estado": "string" }`
- **POST `/cajas/{uuid}/verificar`**
  - multipart/form-data con imágenes
  - Response: `{ "objetos_detectados": [], "faltantes": [], "sobrantes": [] }`
- **GET `/cajas/{uuid}/objetos-esperados`**
  - Response: `[ { "id_objeto": number, "cantidad": number } ]`
- **PUT `/cajas/{uuid}/ubicacion`**
  - Request: `{ "ubicacion": "string" }`
  - Response: `{ "uuid": "uuid", "ubicacion": "string" }`
- **GET `/cajas`**
  - Query params opcionales: `estado`, `tipo`, `ubicacion`, `fecha_ultima_verificacion`
  - Response: `{ "data": [ ... ], "page": 1 }`
- **GET `/cajas/{uuid}/historial`**
  - Response: `[ { "tipo_evento": "string", "timestamp": "ISO" } ]`

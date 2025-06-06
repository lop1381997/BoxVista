# Esquema de Base de Datos

A continuación se describe el esquema inicial de la base de datos para BoxVista.

## Tablas

### Cajas
- `uuid` (UUID, **PK**)
- `tipo` (VARCHAR)
- `fecha_creacion` (TIMESTAMP, default NOW)
- `ubicacion` (VARCHAR)
- `estado` (VARCHAR)

### Objetos
- `id` (SERIAL, **PK**)
- `nombre` (VARCHAR)
- `descripcion` (TEXT)
- `categoria` (VARCHAR)
- `peso` (NUMERIC)
- `dimensiones` (VARCHAR)
- `etiquetas` (VARCHAR[]) **opcional**

### CajaObjeto
- `uuid_caja` (UUID, **FK -> Cajas.uuid**)
- `id_objeto` (INTEGER, **FK -> Objetos.id**)
- `cantidad` (INTEGER)
- `estado` (VARCHAR)
- `timestamp_verificacion` (TIMESTAMP)

### HistorialEventos
- `uuid_evento` (UUID, **PK**)
- `uuid_caja` (UUID, **FK -> Cajas.uuid**)
- `tipo_evento` (VARCHAR)
- `usuario` (VARCHAR)
- `timestamp` (TIMESTAMP)
- `detalles` (JSONB)

## Relaciones
- **Cajas** 1--N **CajaObjeto**
- **Objetos** 1--N **CajaObjeto**
- **Cajas** 1--N **HistorialEventos**

Este esquema puede evolucionar según se añadan más módulos (p. ej. usuarios, sensores, etc.).

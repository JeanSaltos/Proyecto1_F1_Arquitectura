# 📖 Explicación del Funcionamiento y Escenarios de Prueba — LogiFlow (Fase 1)

---

## Parte 1: Explicación del Funcionamiento

### 1.1 ¿Cómo interactúan los componentes de la Fase 1?

La Fase 1 implementa dos microservicios independientes que representan dos Bounded Contexts distintos del dominio de LogiFlow:

```
┌─────────────────────────────────────────────────────────────────┐
│                     FLUJO DE LA FASE 1                          │
│                                                                  │
│  [Administrador]                    [Taller Externo]            │
│       │                                   │                      │
│       ▼                                   ▼                      │
│  ┌──────────┐   REST/JSON          ┌──────────┐   SOAP/XML     │
│  │ Swagger  │ ◄─────────────►      │  SoapUI  │ ◄──────────►   │
│  │   UI     │                      │  / cURL  │                 │
│  └────┬─────┘                      └────┬─────┘                 │
│       │                                  │                       │
│       ▼                                  ▼                       │
│  ┌──────────────────┐            ┌──────────────────┐           │
│  │  ms-flota-rest   │            │  ms-taller-soap  │           │
│  │  (Puerto 8081)   │            │  (Puerto 8082)   │           │
│  │                  │            │                   │           │
│  │  Controller      │            │  Endpoint SOAP    │           │
│  │      │           │            │      │            │           │
│  │  Service         │            │  Service (ACL)    │           │
│  │      │           │            │      │            │           │
│  │  Repository      │            │  Repository       │           │
│  │      │           │            │  (In-Memory)      │           │
│  │  PostgreSQL      │            │                   │           │
│  └──────────────────┘            └──────────────────┘           │
└─────────────────────────────────────────────────────────────────┘
```

#### Flujo típico ms-flota-rest:
1. El administrador accede a Swagger UI (`http://localhost:8081/swagger-ui.html`).
2. Envía una petición REST (ej: `POST /api/vehiculos` con body JSON).
3. El `VehiculoController` recibe la petición y valida el DTO con `@Valid`.
4. Si hay errores de validación, el `GlobalExceptionHandler` retorna un HTTP 400 con detalles.
5. Si la validación pasa, el controlador delega al `VehiculoServiceImpl`.
6. El servicio aplica reglas de negocio (ej: verificar matrícula única).
7. Si se viola una regla, lanza `BusinessException` → HTTP 409.
8. Si todo es correcto, persiste en PostgreSQL vía `VehiculoRepository`.
9. Retorna el objeto creado con HTTP 201.

#### Flujo típico ms-taller-soap:
1. El taller externo envía un sobre SOAP (XML) al endpoint `http://localhost:8082/ws`.
2. Spring WS deserializa el XML usando las clases JAXB generadas desde el XSD.
3. El `TallerEndpoint` recibe el request tipado y delega al `TallerServiceImpl`.
4. El servicio usa `TallerTranslator` (ACL) para traducir entre tipos JAXB y modelos internos.
5. El repositorio (in-memory) procesa la consulta o registra la orden.
6. El resultado se traduce de vuelta a tipos JAXB por la ACL.
7. Spring WS serializa la respuesta como XML y la retorna al taller.

### 1.2 ¿Cómo resuelve la problemática del monolito?

| Problema del Monolito                  | Solución con Microservicios                                     |
|----------------------------------------|-----------------------------------------------------------------|
| Acoplamiento total                     | Cada BC tiene su propio código, BD y despliegue independiente  |
| No se puede escalar selectivamente     | Cada servicio escala de forma autónoma                         |
| BD compartida con bloqueos             | `ms-flota-rest` usa PostgreSQL propia; `ms-taller-soap` es independiente |
| Equipos bloqueados entre sí            | Equipos trabajan en repos/servicios distintos sin interferir   |
| Tecnología homogénea forzada           | REST para gestión interna, SOAP para integración con terceros  |
| Cambios en taller afectan a flota      | La ACL aísla cambios del contrato SOAP del dominio de flota   |

---

## Parte 2: Explicación del DDD Aplicado

### 2.1 ¿Por qué estos 10 Bounded Contexts?

La definición de los 10 BCs sigue el principio fundamental de DDD: **cada contexto delimita un modelo de dominio con su propio lenguaje y reglas de negocio**.

#### Criterios de separación aplicados:

1. **Cohesión semántica:** `Vehiculo` en Flota tiene `capacidad` y `estado`; `VehiculoTaller` en Mantenimiento tiene `marca`, `modelo` y `anio`. Mismo concepto, diferente significado → diferentes BCs.

2. **Independencia de ciclo de vida:** Un cambio en cómo se calculan rutas (BC-03) no debe forzar un redespliegue del CRUD de vehículos (BC-01).

3. **Responsabilidad única:** Cada BC tiene un equipo responsable y un propósito claro. Facturación (BC-07) no debe conocer los detalles de rastreo GPS (BC-06).

4. **Protocolos naturales:** Flota expone REST porque sus consumidores son internos. Taller expone SOAP porque el sistema externo del taller así lo requiere.

### 2.2 ¿Cómo se relacionan según el Context Map?

```
Gestión de Flota ──[Upstream/Downstream]──► Ruteo y Despacho
       │
       │ La flota PROVEE recursos (vehículos disponibles, conductores)
       │ al módulo de despacho que los CONSUME para crear asignaciones.
       │
Taller Externo ──[ACL]──► Gestión de Flota
       │
       │ El taller es un SISTEMA EXTERNO con su propio modelo.
       │ La ACL (TallerTranslator) traduce su lenguaje al nuestro
       │ para que cambios en el contrato SOAP no contaminen el dominio.
```

**¿Por qué ACL y no Shared Kernel?** Porque el taller es un sistema **externo** que no controlamos. Un Shared Kernel implica que ambos equipos comparten y co-evolucionan un modelo. Con un tercero eso es imposible, por lo que usamos una capa de traducción que **absorbe** sus cambios.

### 2.3 ¿Cómo el código refleja el Lenguaje Ubicuo?

| Término del Negocio          | Clase/Método en Código                                   | Ubicación                      |
|------------------------------|----------------------------------------------------------|--------------------------------|
| "Registrar un vehículo"      | `VehiculoController.crear()`, `IVehiculoService.crear()` | ms-flota-rest                  |
| "Matrícula"                  | `vehiculo.getMatricula()`, DTO con `@Pattern`            | ms-flota-rest                  |
| "Vehículo disponible"        | `EstadoVehiculo.DISPONIBLE`, endpoint `/disponibles`     | ms-flota-rest                  |
| "Conductor apto"             | `conductor.getDisponible()`, `/api/conductores/disponibles` | ms-flota-rest              |
| "Consultar vehículo" (SOAP)  | `TallerEndpoint.consultarVehiculo()`                     | ms-taller-soap                 |
| "Orden de mantenimiento"     | `OrdenMantenimiento.java`, `registrarOrden()`            | ms-taller-soap                 |
| "Capa Anticorrupción"        | Paquete `anticorruption/`, clase `TallerTranslator`      | ms-taller-soap                 |

> **Nota:** Los nombres de clases, métodos, variables y endpoints usan exactamente los mismos términos que usa el negocio. No hay traducciones arbitrarias como "Car" o "Driver" — se dice "Vehiculo" y "Conductor" porque así habla el dominio logístico ecuatoriano.

---

## Parte 3: Escenarios de Prueba

### 3.1 Pruebas del Microservicio REST (ms-flota-rest)

> **Prerrequisito:** El servicio debe estar corriendo en `http://localhost:8081`

---

#### 📋 ESCENARIO 1: Crear un vehículo exitosamente

**Descripción:** Registrar un nuevo vehículo con datos válidos.

```bash
curl -X POST http://localhost:8081/api/vehiculos \
  -H "Content-Type: application/json" \
  -d '{
    "matricula": "ABC-1234",
    "tipo": "CAMION",
    "capacidad": 8500.0,
    "estado": "DISPONIBLE"
  }'
```

**Respuesta esperada (HTTP 201):**
```json
{
  "id": 1,
  "matricula": "ABC-1234",
  "tipo": "CAMION",
  "capacidad": 8500.0,
  "estado": "DISPONIBLE"
}
```

---

#### 📋 ESCENARIO 2: Error de validación — matrícula con formato inválido

**Descripción:** Intentar registrar un vehículo con matrícula que no cumple el patrón.

```bash
curl -X POST http://localhost:8081/api/vehiculos \
  -H "Content-Type: application/json" \
  -d '{
    "matricula": "123-ABC",
    "tipo": "MOTO",
    "capacidad": 100.0,
    "estado": "DISPONIBLE"
  }'
```

**Respuesta esperada (HTTP 400):**
```json
{
  "timestamp": "2026-05-13T...",
  "status": 400,
  "error": "Error de Validación",
  "detalles": {
    "matricula": "Formato de matrícula inválido (Ej: ABC-1234)"
  }
}
```

---

#### 📋 ESCENARIO 3: Error de negocio — matrícula duplicada

**Descripción:** Intentar registrar un vehículo con una matrícula que ya existe.

```bash
# Primero crear el vehículo
curl -X POST http://localhost:8081/api/vehiculos \
  -H "Content-Type: application/json" \
  -d '{"matricula":"DEF-5678","tipo":"FURGONETA","capacidad":3000.0,"estado":"DISPONIBLE"}'

# Intentar crear otro con la misma matrícula
curl -X POST http://localhost:8081/api/vehiculos \
  -H "Content-Type: application/json" \
  -d '{"matricula":"DEF-5678","tipo":"MOTO","capacidad":50.0,"estado":"DISPONIBLE"}'
```

**Respuesta esperada (HTTP 409):**
```json
{
  "timestamp": "2026-05-13T...",
  "status": 409,
  "error": "Conflicto de Negocio",
  "mensaje": "La matrícula DEF-5678 ya está registrada en la flota"
}
```

---

#### 📋 ESCENARIO 4: Error de validación — campos obligatorios faltantes

**Descripción:** Enviar un body vacío o con campos nulos.

```bash
curl -X POST http://localhost:8081/api/vehiculos \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Respuesta esperada (HTTP 400):**
```json
{
  "timestamp": "2026-05-13T...",
  "status": 400,
  "error": "Error de Validación",
  "detalles": {
    "matricula": "La matrícula es obligatoria",
    "tipo": "El tipo de vehículo es obligatorio",
    "capacidad": "La capacidad es obligatoria",
    "estado": "El estado es obligatorio"
  }
}
```

---

#### 📋 ESCENARIO 5: Obtener un vehículo por ID inexistente

```bash
curl -X GET http://localhost:8081/api/vehiculos/9999
```

**Respuesta esperada (HTTP 404):**
```json
{
  "timestamp": "2026-05-13T...",
  "status": 404,
  "error": "Recurso No Encontrado",
  "mensaje": "Vehículo no encontrado con ID: 9999"
}
```

---

#### 📋 ESCENARIO 6: Listar todos los vehículos

```bash
curl -X GET http://localhost:8081/api/vehiculos
```

**Respuesta esperada (HTTP 200):**
```json
[
  {
    "id": 1,
    "matricula": "ABC-1234",
    "tipo": "CAMION",
    "capacidad": 8500.0,
    "estado": "DISPONIBLE"
  }
]
```

---

#### 📋 ESCENARIO 7: Consultar vehículos disponibles

```bash
curl -X GET http://localhost:8081/api/vehiculos/disponibles
```

**Respuesta esperada (HTTP 200):** Solo vehículos con estado `DISPONIBLE`.

---

#### 📋 ESCENARIO 8: Actualizar estado de un vehículo

```bash
curl -X PATCH "http://localhost:8081/api/vehiculos/1/estado?nuevoEstado=MANTENIMIENTO"
```

**Respuesta esperada (HTTP 200):**
```json
{
  "id": 1,
  "matricula": "ABC-1234",
  "tipo": "CAMION",
  "capacidad": 8500.0,
  "estado": "MANTENIMIENTO"
}
```

---

#### 📋 ESCENARIO 9: Estado inválido

```bash
curl -X PATCH "http://localhost:8081/api/vehiculos/1/estado?nuevoEstado=DESTRUIDO"
```

**Respuesta esperada (HTTP 400):**
```json
{
  "timestamp": "2026-05-13T...",
  "status": 400,
  "error": "Argumento Inválido",
  "mensaje": "Estado inválido: 'DESTRUIDO'. Estados permitidos: DISPONIBLE, EN_SERVICIO, MANTENIMIENTO"
}
```

---

#### 📋 ESCENARIO 10: Actualizar un vehículo completo

```bash
curl -X PUT http://localhost:8081/api/vehiculos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "matricula": "ABC-1234",
    "tipo": "FURGONETA",
    "capacidad": 4000.0,
    "estado": "EN_SERVICIO"
  }'
```

**Respuesta esperada (HTTP 200):**
```json
{
  "id": 1,
  "matricula": "ABC-1234",
  "tipo": "FURGONETA",
  "capacidad": 4000.0,
  "estado": "EN_SERVICIO"
}
```

---

#### 📋 ESCENARIO 11: Eliminar un vehículo

```bash
curl -X DELETE http://localhost:8081/api/vehiculos/1
```

**Respuesta esperada (HTTP 204):** Sin body (No Content).

---

#### 📋 ESCENARIO 12: Eliminar vehículo inexistente

```bash
curl -X DELETE http://localhost:8081/api/vehiculos/9999
```

**Respuesta esperada (HTTP 404):**
```json
{
  "timestamp": "2026-05-13T...",
  "status": 404,
  "error": "Recurso No Encontrado",
  "mensaje": "No se puede eliminar: Vehículo con ID 9999 no encontrado"
}
```

---

#### 📋 ESCENARIO 13: Registrar un conductor exitosamente

```bash
curl -X POST http://localhost:8081/api/conductores \
  -H "Content-Type: application/json" \
  -d '{
    "cedula": "1723456789",
    "nombre": "Carlos Mendoza",
    "licencia": "Tipo C",
    "disponible": true
  }'
```

**Respuesta esperada (HTTP 201):**
```json
{
  "id": 1,
  "cedula": "1723456789",
  "nombre": "Carlos Mendoza",
  "licencia": "Tipo C",
  "disponible": true
}
```

---

#### 📋 ESCENARIO 14: Error de validación — cédula con formato inválido

```bash
curl -X POST http://localhost:8081/api/conductores \
  -H "Content-Type: application/json" \
  -d '{
    "cedula": "12345",
    "nombre": "Test",
    "licencia": "Tipo B",
    "disponible": true
  }'
```

**Respuesta esperada (HTTP 400):**
```json
{
  "timestamp": "2026-05-13T...",
  "status": 400,
  "error": "Error de Validación",
  "detalles": {
    "cedula": "La cédula debe tener 10 dígitos"
  }
}
```

---

#### 📋 ESCENARIO 15: Listar conductores disponibles

```bash
curl -X GET http://localhost:8081/api/conductores/disponibles
```

**Respuesta esperada (HTTP 200):** Solo conductores con `disponible: true`.

---

### 3.2 Pruebas del Microservicio SOAP (ms-taller-soap)

> **Prerrequisito:** El servicio debe estar corriendo en `http://localhost:8082`
>
> **WSDL:** `http://localhost:8082/ws/taller.wsdl`

Las pruebas SOAP se realizan enviando envoltorios XML al endpoint `http://localhost:8082/ws`.

---

#### 📋 ESCENARIO 16: Consultar vehículo existente (PBC1234)

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ws="http://espe.edu.ec/taller/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:ConsultarVehiculoRequest>
         <ws:matricula>PBC1234</ws:matricula>
      </ws:ConsultarVehiculoRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

**Respuesta esperada (HTTP 200 — XML):**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <ns2:ConsultarVehiculoResponse xmlns:ns2="http://espe.edu.ec/taller/ws">
         <ns2:vehiculo>
            <ns2:matricula>PBC1234</ns2:matricula>
            <ns2:marca>Toyota</ns2:marca>
            <ns2:modelo>Corolla</ns2:modelo>
            <ns2:anio>2020</ns2:anio>
         </ns2:vehiculo>
      </ns2:ConsultarVehiculoResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

#### 📋 ESCENARIO 17: Consultar segundo vehículo (XYZ9876)

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ws="http://espe.edu.ec/taller/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:ConsultarVehiculoRequest>
         <ws:matricula>XYZ9876</ws:matricula>
      </ws:ConsultarVehiculoRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

**Respuesta esperada:** Datos de Honda Civic 2022.

---

#### 📋 ESCENARIO 18: Consultar vehículo inexistente

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ws="http://espe.edu.ec/taller/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:ConsultarVehiculoRequest>
         <ws:matricula>NOEXISTE</ws:matricula>
      </ws:ConsultarVehiculoRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

**Respuesta esperada (HTTP 200 — XML):**
```xml
<ns2:ConsultarVehiculoResponse xmlns:ns2="http://espe.edu.ec/taller/ws">
   <ns2:vehiculo>
      <ns2:matricula>NO_ENCONTRADO</ns2:matricula>
      <ns2:marca>N/A</ns2:marca>
      <ns2:modelo>N/A</ns2:modelo>
      <ns2:anio>0</ns2:anio>
   </ns2:vehiculo>
</ns2:ConsultarVehiculoResponse>
```

---

#### 📋 ESCENARIO 19: Registrar orden de mantenimiento exitosamente

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ws="http://espe.edu.ec/taller/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:RegistrarOrdenMantenimientoRequest>
         <ws:matricula>PBC1234</ws:matricula>
         <ws:descripcion>Cambio de aceite y filtros. Revisión de frenos traseros.</ws:descripcion>
      </ws:RegistrarOrdenMantenimientoRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

**Respuesta esperada (HTTP 200 — XML):**
```xml
<ns2:RegistrarOrdenMantenimientoResponse xmlns:ns2="http://espe.edu.ec/taller/ws">
   <ns2:mensaje>Orden de mantenimiento registrada exitosamente para vehículo PBC1234</ns2:mensaje>
   <ns2:idOrden>ORD-1715612345678</ns2:idOrden>
</ns2:RegistrarOrdenMantenimientoResponse>
```

> **Nota:** El `idOrden` es generado dinámicamente con timestamp, por lo que su valor exacto varía.

---

#### 📋 ESCENARIO 20: Registrar mantenimiento correctivo urgente

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ws="http://espe.edu.ec/taller/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:RegistrarOrdenMantenimientoRequest>
         <ws:matricula>XYZ9876</ws:matricula>
         <ws:descripcion>URGENTE: Falla en sistema de transmisión. Vehículo inmovilizado en ruta.</ws:descripcion>
      </ws:RegistrarOrdenMantenimientoRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

**Respuesta esperada:** Confirmación con nuevo `idOrden`.

---

#### 📋 ESCENARIO 21: Verificar WSDL generado

```bash
curl -X GET http://localhost:8082/ws/taller.wsdl
```

**Respuesta esperada:** Documento WSDL completo con las dos operaciones (`consultarVehiculo` y `registrarOrdenMantenimiento`), types del XSD, y binding SOAP.

---

#### 📋 ESCENARIO 22: Request SOAP mal formado (XML inválido)

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0"?>
<soapenv:Envelope>
   <soapenv:Body>
      <INVALID>
   </soapenv:Body>
</soapenv:Envelope>'
```

**Respuesta esperada:** SOAP Fault con error de parsing XML.

---

### 3.3 Pruebas de Integración del Pipeline CI/CD

| Escenario                              | Cómo verificar                                                |
|----------------------------------------|---------------------------------------------------------------|
| Pipeline se ejecuta en push a `main`   | Hacer push a `main` y verificar en pestaña Actions de GitHub |
| Pipeline se ejecuta en push a `dev`    | Hacer push a `development` y verificar                       |
| Build falla por test roto              | Romper un test intencionalmente y verificar notificación     |
| SonarCloud reporta code smells         | Verificar dashboard en sonarcloud.io después del análisis    |
| Telegram recibe notificación           | Verificar el grupo/chat configurado tras un push             |

---

### 3.4 Pruebas con Swagger UI (ms-flota-rest)

Para una experiencia visual de testing:

1. Abrir `http://localhost:8081/swagger-ui.html` en el navegador.
2. Expandir cualquier endpoint (ej: `POST /api/vehiculos`).
3. Click en **"Try it out"**.
4. Modificar el body JSON según el escenario deseado.
5. Click en **"Execute"**.
6. Verificar el código HTTP y el body de respuesta.

---

## Resumen de Escenarios

| #  | Tipo    | Escenario                                      | HTTP Esperado |
|----|---------|-------------------------------------------------|---------------|
| 1  | REST    | Crear vehículo exitosamente                     | 201           |
| 2  | REST    | Matrícula con formato inválido                  | 400           |
| 3  | REST    | Matrícula duplicada                             | 409           |
| 4  | REST    | Campos obligatorios faltantes                   | 400           |
| 5  | REST    | Buscar por ID inexistente                       | 404           |
| 6  | REST    | Listar todos los vehículos                      | 200           |
| 7  | REST    | Consultar vehículos disponibles                 | 200           |
| 8  | REST    | Actualizar estado operativo                     | 200           |
| 9  | REST    | Estado inválido                                 | 400           |
| 10 | REST    | Actualizar vehículo completo                    | 200           |
| 11 | REST    | Eliminar vehículo                               | 204           |
| 12 | REST    | Eliminar vehículo inexistente                   | 404           |
| 13 | REST    | Registrar conductor exitosamente                | 201           |
| 14 | REST    | Cédula con formato inválido                     | 400           |
| 15 | REST    | Listar conductores disponibles                  | 200           |
| 16 | SOAP    | Consultar vehículo existente (PBC1234)          | 200 (XML)     |
| 17 | SOAP    | Consultar segundo vehículo (XYZ9876)            | 200 (XML)     |
| 18 | SOAP    | Consultar vehículo inexistente                  | 200 (XML)     |
| 19 | SOAP    | Registrar orden de mantenimiento                | 200 (XML)     |
| 20 | SOAP    | Registrar mantenimiento urgente                 | 200 (XML)     |
| 21 | SOAP    | Verificar WSDL generado                         | 200 (WSDL)    |
| 22 | SOAP    | Request XML mal formado                         | SOAP Fault    |

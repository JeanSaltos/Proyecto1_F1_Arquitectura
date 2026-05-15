# 🔧 Microservicio Taller SOAP (ms-taller-soap)

> Bounded Context: Taller y Mantenimiento — Capa Anticorrupción (ACL)

Este microservicio expone **exclusivamente** una interfaz SOAP para operaciones de un taller externo, implementando el patrón **Anti-Corruption Layer (Capa Anticorrupción)** de DDD.

## Arquitectura Interna

```
┌──────────────────────────────────────────────────┐
│                ms-taller-soap                     │
│                                                   │
│  [Endpoint SOAP]                                  │
│       │  ↕ Tipos JAXB (generados del XSD)        │
│       ▼                                           │
│  [ITallerService]                                 │
│       │  ↕ TallerTranslator (ACL)                │
│       ▼                                           │
│  [Modelos Internos]  ←→  [TallerRepository]      │
│  (VehiculoTaller,        (In-Memory)              │
│   OrdenMantenimiento)                             │
└──────────────────────────────────────────────────┘
```

## Requisitos

- Java 17
- Maven 3.6+

## Generación del Contrato (WSDL)

El servicio utiliza la estrategia **"Contract-First"**. El contrato base está definido en:
`src/main/resources/xsd/taller.xsd`

Al compilar (`mvn clean compile`), el plugin `jaxb2-maven-plugin` genera automáticamente las clases Java en `target/generated-sources/jaxb` bajo el paquete `ec.edu.espe.taller.ws`.

## Operaciones SOAP

| Operación                         | Entrada                    | Salida                           |
|----------------------------------|----------------------------|----------------------------------|
| `consultarVehiculo`              | `matricula` (String)       | `Vehiculo` (matricula, marca, modelo, anio) |
| `registrarOrdenMantenimiento`    | `matricula`, `descripcion` | `mensaje`, `idOrden`             |

**Namespace:** `http://espe.edu.ec/taller/ws`

## Ejecución

```bash
cd ms-taller-soap
mvn clean compile
mvn spring-boot:run
```

- **Puerto:** 8082
- **WSDL:** [http://localhost:8082/ws/taller.wsdl](http://localhost:8082/ws/taller.wsdl)

## Capa Anticorrupción (ACL)

El paquete `ec.edu.espe.taller.anticorruption` contiene:

| Clase                  | Responsabilidad                                              |
|------------------------|-------------------------------------------------------------|
| `VehiculoTaller`       | Modelo interno del dominio (NO depende de JAXB)             |
| `OrdenMantenimiento`   | Modelo interno para órdenes (NO depende de JAXB)            |
| `TallerTranslator`     | Traduce entre tipos JAXB ↔ modelos internos                 |

## Ejemplos de Petición

### consultarVehiculo

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://espe.edu.ec/taller/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:ConsultarVehiculoRequest>
         <ws:matricula>PBC1234</ws:matricula>
      </ws:ConsultarVehiculoRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### registrarOrdenMantenimiento

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://espe.edu.ec/taller/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:RegistrarOrdenMantenimientoRequest>
         <ws:matricula>PBC1234</ws:matricula>
         <ws:descripcion>Cambio de aceite y revisión de frenos</ws:descripcion>
      </ws:RegistrarOrdenMantenimientoRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

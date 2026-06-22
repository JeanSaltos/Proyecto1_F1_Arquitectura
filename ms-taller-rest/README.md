# Microservicio Taller SOAP (ms-taller-soap)

Bounded Context: Taller y Mantenimiento. Este servicio expone exclusivamente SOAP mediante Spring Web Services.

## Contrato

- XSD: `src/main/resources/xsd/taller.xsd`
- WSDL: `http://localhost:8082/ws/taller.wsdl`
- Endpoint SOAP: `http://localhost:8082/ws`
- Namespace: `http://espe.edu.ec/taller/ws`

## Operaciones

| Operacion | Request | Response |
|---|---|---|
| `consultarVehiculo` | `consultarVehiculoRequest(matricula)` | `consultarVehiculoResponse(vehiculo)` |
| `registrarOrdenMantenimiento` | `registrarOrdenMantenimientoRequest(matricula, descripcion)` | `registrarOrdenMantenimientoResponse(idOrden, matricula, estado, fechaRegistro, mensaje)` |

## Ejecucion local

```bash
cd ms-taller-rest
mvn clean verify
mvn spring-boot:run
```

## Probar WSDL

```bash
curl http://localhost:8082/ws/taller.wsdl
```

## Probar con archivos XML

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml;charset=UTF-8" \
  --data-binary @soap-requests/consultar-vehiculo.xml
```

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml;charset=UTF-8" \
  --data-binary @soap-requests/registrar-orden.xml
```

## consultarVehiculo

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml;charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ws="http://espe.edu.ec/taller/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:consultarVehiculoRequest>
         <ws:matricula>PBC1234</ws:matricula>
      </ws:consultarVehiculoRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

## registrarOrdenMantenimiento

```bash
curl -X POST http://localhost:8082/ws \
  -H "Content-Type: text/xml;charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:ws="http://espe.edu.ec/taller/ws">
   <soapenv:Header/>
   <soapenv:Body>
      <ws:registrarOrdenMantenimientoRequest>
         <ws:matricula>PBC1234</ws:matricula>
         <ws:descripcion>Cambio de aceite y revision de frenos</ws:descripcion>
      </ws:registrarOrdenMantenimientoRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

# RabbitMQ Evidence

Exchange esperado: `logiflow-exchange`.

Eventos requeridos:
- `pedido.creado`
- `pedido.cancelado`
- `envio.asignado`
- `posicion.actualizada`

Prueba:
```powershell
$env:RABBITMQ_DEFAULT_USER="<usuario>"
$env:RABBITMQ_DEFAULT_PASS="<password>"
.\tests\rabbitmq\rabbitmq-events.ps1 -ManagementUrl http://localhost:15672
```

Logs esperados:
```text
OK RabbitMQ event pedido.creado: {...}
OK RabbitMQ event pedido.cancelado: {...}
OK RabbitMQ event envio.asignado: {...}
OK RabbitMQ event posicion.actualizada: {...}
```

Logs esperados en servicios:
```text
Evento recibido 'pedido.creado' para Pedido ID: ...
Evento recibido 'pedido.cancelado' para Pedido ID: ...
Evento 'envio.asignado' publicado para Envio ID: ...
Evento 'posicion.actualizada' publicado para Envio ID: ...
GPS recibido - EnvioId: ...
BFF Gateway capturo coordenadas GPS para Envio ID: ...
```

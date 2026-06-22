param(
    [string]$ManagementUrl = "http://localhost:15672"
)

$ErrorActionPreference = "Stop"

$User = $env:RABBITMQ_DEFAULT_USER
$Pass = $env:RABBITMQ_DEFAULT_PASS

if ([string]::IsNullOrWhiteSpace($User) -or [string]::IsNullOrWhiteSpace($Pass)) {
    throw "Define RABBITMQ_DEFAULT_USER y RABBITMQ_DEFAULT_PASS antes de ejecutar."
}

$authBytes = [Text.Encoding]::ASCII.GetBytes("${User}:${Pass}")
$headers = @{
    Authorization = "Basic " + [Convert]::ToBase64String($authBytes)
}

$exchange = "logiflow-exchange"
$events = @(
    @{ key = "pedido.creado"; queue = "evidence-pedido-creado"; payload = '{"id":"00000000-0000-0000-0000-000000000101","clienteId":"00000000-0000-0000-0000-000000000001","estado":"CREADO"}' },
    @{ key = "pedido.cancelado"; queue = "evidence-pedido-cancelado"; payload = '{"id":"00000000-0000-0000-0000-000000000102","estado":"CANCELADO"}' },
    @{ key = "envio.asignado"; queue = "evidence-envio-asignado"; payload = '{"id":"00000000-0000-0000-0000-000000000201","pedidoId":"00000000-0000-0000-0000-000000000101","estado":"ASIGNADO"}' },
    @{ key = "posicion.actualizada"; queue = "evidence-posicion-actualizada"; payload = '{"envioId":"00000000-0000-0000-0000-000000000201","lat":-0.1807,"lng":-78.4678,"velocidad":45.0}' }
)

Invoke-RestMethod -Method Put -Uri "$ManagementUrl/api/exchanges/%2F/$exchange" -Headers $headers -ContentType "application/json" -Body '{"type":"topic","durable":true}'

foreach ($event in $events) {
    $queueBody = '{"durable":false,"auto_delete":true}'
    Invoke-RestMethod -Method Put -Uri "$ManagementUrl/api/queues/%2F/$($event.queue)" -Headers $headers -ContentType "application/json" -Body $queueBody

    $bindingBody = @{ routing_key = $event.key; arguments = @{} } | ConvertTo-Json -Depth 5
    Invoke-RestMethod -Method Post -Uri "$ManagementUrl/api/bindings/%2F/e/$exchange/q/$($event.queue)" -Headers $headers -ContentType "application/json" -Body $bindingBody

    $publishBody = @{
        properties = @{}
        routing_key = $event.key
        payload = $event.payload
        payload_encoding = "string"
    } | ConvertTo-Json -Depth 5

    $publish = Invoke-RestMethod -Method Post -Uri "$ManagementUrl/api/exchanges/%2F/$exchange/publish" -Headers $headers -ContentType "application/json" -Body $publishBody
    if (-not $publish.routed) {
        throw "Evento $($event.key) no fue ruteado"
    }

    $getBody = '{"count":1,"ackmode":"ack_requeue_false","encoding":"auto","truncate":50000}'
    $message = Invoke-RestMethod -Method Post -Uri "$ManagementUrl/api/queues/%2F/$($event.queue)/get" -Headers $headers -ContentType "application/json" -Body $getBody
    if ($message.Count -eq 0) {
        throw "No se recibio evidencia para $($event.key)"
    }

    Write-Host "OK RabbitMQ event $($event.key): $($message[0].payload)"
}

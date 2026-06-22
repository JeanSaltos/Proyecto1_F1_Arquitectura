param(
    [string]$WebSocketUrl = "ws://localhost/ws/seguimiento",
    [string]$EnvioId = "00000000-0000-0000-0000-000000000201"
)

$ErrorActionPreference = "Stop"

$client = [System.Net.WebSockets.ClientWebSocket]::new()
$uri = [Uri]::new($WebSocketUrl)
$client.ConnectAsync($uri, [Threading.CancellationToken]::None).GetAwaiter().GetResult()

function Send-StompFrame {
    param([string]$Frame)
    $bytes = [Text.Encoding]::UTF8.GetBytes($Frame + [char]0)
    $segment = [ArraySegment[byte]]::new($bytes)
    $client.SendAsync($segment, [System.Net.WebSockets.WebSocketMessageType]::Text, $true, [Threading.CancellationToken]::None).GetAwaiter().GetResult()
}

Send-StompFrame "CONNECT`naccept-version:1.2`nheart-beat:10000,10000`n`n"
Send-StompFrame "SUBSCRIBE`nid:sub-0`ndestination:/topic/seguimiento/$EnvioId`n`n"

Write-Host "WebSocket/STOMP conectado y suscrito a /topic/seguimiento/$EnvioId"
Write-Host "Publica posicion.actualizada para ver MESSAGE en el cliente."

$client.CloseAsync([System.Net.WebSockets.WebSocketCloseStatus]::NormalClosure, "test done", [Threading.CancellationToken]::None).GetAwaiter().GetResult()

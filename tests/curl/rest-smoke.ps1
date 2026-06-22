param(
    [string]$BaseUrl = "http://localhost"
)

$ErrorActionPreference = "Stop"

function Invoke-JsonPost {
    param(
        [string]$Url,
        [hashtable]$Body
    )
    Invoke-RestMethod `
        -Method Post `
        -Uri $Url `
        -ContentType "application/json" `
        -Body ($Body | ConvertTo-Json -Depth 8)
}

$suffix = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$username = "qa_$suffix"
$password = "ChangeMe_$suffix!"

Write-Host "REST smoke against $BaseUrl"

Write-Host "1. Auth register/login/verify"
$register = Invoke-JsonPost "$BaseUrl/api/auth/register" @{
    username = $username
    password = $password
    rol = "ADMIN"
}
$login = Invoke-JsonPost "$BaseUrl/api/auth/login" @{
    username = $username
    password = $password
}
Invoke-RestMethod -Uri "$BaseUrl/api/auth/verify" -Headers @{
    Authorization = "Bearer $($login.token)"
}

Write-Host "2. REST reads"
Invoke-RestMethod -Uri "$BaseUrl/api/vehiculos"
Invoke-RestMethod -Uri "$BaseUrl/api/conductores"
Invoke-RestMethod -Uri "$BaseUrl/api/clientes"
Invoke-RestMethod -Uri "$BaseUrl/api/pedidos"
Invoke-RestMethod -Uri "$BaseUrl/api/ruteo/envios"

Write-Host "REST smoke OK"

param(
    [string]$BaseUrl = "http://localhost",
    [string]$RequestFile = "ms-taller-rest/soap-requests/consultar-vehiculo.xml"
)

$ErrorActionPreference = "Stop"

Write-Host "1. WSDL"
Invoke-WebRequest -Uri "$BaseUrl/ws/taller.wsdl" -UseBasicParsing

Write-Host "2. SOAP request"
Invoke-WebRequest `
    -Method Post `
    -Uri "$BaseUrl/ws" `
    -ContentType "text/xml; charset=utf-8" `
    -InFile $RequestFile `
    -UseBasicParsing

Write-Host "SOAP smoke OK"

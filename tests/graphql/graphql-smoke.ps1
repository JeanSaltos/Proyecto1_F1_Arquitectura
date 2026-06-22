param(
    [string]$BaseUrl = "http://localhost",
    [string]$Payload = "tests/graphql/graphql-smoke.json"
)

$ErrorActionPreference = "Stop"

Invoke-RestMethod `
    -Method Post `
    -Uri "$BaseUrl/graphql" `
    -ContentType "application/json" `
    -InFile $Payload

Write-Host "GraphQL smoke OK"

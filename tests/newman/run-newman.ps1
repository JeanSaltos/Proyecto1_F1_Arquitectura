param(
    [string]$Collection = "LogiFlow_Phase2_Postman_Collection.json",
    [string]$BaseUrl = "http://localhost"
)

$ErrorActionPreference = "Stop"

if (-not (Get-Command newman -ErrorAction SilentlyContinue)) {
    throw "Newman no esta instalado. Instala con: npm install -g newman"
}

newman run $Collection --env-var "baseUrl=$BaseUrl" --reporters cli

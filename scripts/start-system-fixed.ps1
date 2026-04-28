# Script para iniciar todo el sistema correctamente

$currentDir = $PSScriptRoot
$root = (Get-Item $currentDir).Parent.FullName

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "   INICIANDO SISTEMA STARK DISTRIBUIDO" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "Ruta detectada: $root" -ForegroundColor Yellow
Write-Host ""

# PASO 1: Iniciar Eureka Server
Write-Host "PASO 1/7: Iniciando EUREKA SERVER en puerto 8761..." -ForegroundColor Green
Write-Host "Espera a ver 'Started' antes de continuar" -ForegroundColor Yellow
Write-Host ""
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-config'; Write-Host '>>> EUREKA SERVER <<<' -ForegroundColor Cyan; ..\mvnw.cmd spring-boot:run"

Write-Host "Esperando 20 segundos a que Eureka inicie..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# PASO 2: Abrir Eureka en navegador
Write-Host "PASO 2/7: Abriendo Eureka en navegador..." -ForegroundColor Green
Start-Process "http://localhost:8761"
Start-Sleep -Seconds 3

# PASO 3: Iniciar Gateway
Write-Host "PASO 3/7: Iniciando API GATEWAY en puerto 8080..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-gateway'; Write-Host '>>> API GATEWAY <<<' -ForegroundColor Cyan; ..\mvnw.cmd spring-boot:run"
Start-Sleep -Seconds 10

# PASO 4: Iniciar stark-access
Write-Host "PASO 4/7: Iniciando ACCESS SERVICE en puerto 8084..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-access'; Write-Host '>>> ACCESS SERVICE <<<' -ForegroundColor Cyan; ..\mvnw.cmd spring-boot:run"
Start-Sleep -Seconds 8

# PASO 5: Iniciar sensor
Write-Host "PASO 5/7: Iniciando SENSOR SERVICE en puerto 8082..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-sensor'; Write-Host '>>> SENSOR SERVICE <<<' -ForegroundColor Cyan; ..\mvnw.cmd spring-boot:run"
Start-Sleep -Seconds 8

# PASO 6: Iniciar alert
Write-Host "PASO 6/7: Iniciando ALERT SERVICE en puerto 8083..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-alert'; Write-Host '>>> ALERT SERVICE <<<' -ForegroundColor Cyan; ..\mvnw.cmd spring-boot:run"
Start-Sleep -Seconds 8

# PASO 7: Iniciar Frontend
Write-Host "PASO 7/7: Iniciando FRONTEND en puerto 8085..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-frontend'; Write-Host '>>> FRONTEND <<<' -ForegroundColor Cyan; ..\mvnw.cmd spring-boot:run"

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "   SERVICIOS INICIANDOSE..." -ForegroundColor Green
Write-Host "   Espera 30-40 segundos mas para que todos se registren" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host "URLs IMPORTANTES:" -ForegroundColor Green
Write-Host " - Frontend: http://localhost:8085/stark-security" -ForegroundColor Magenta
Write-Host " - Eureka:   http://localhost:8761" -ForegroundColor Cyan
Write-Host " - Gateway:  http://localhost:8080" -ForegroundColor Cyan
Write-Host " - Access:   http://localhost:8084/api/access" -ForegroundColor Cyan
Write-Host " - Sensor:   http://localhost:8082/api/sensors" -ForegroundColor Cyan
Write-Host " - Alert:    http://localhost:8083/api/alerts" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Green

Start-Sleep -Seconds 45

Write-Host "Abriendo Frontend y Eureka para verificar el sistema..." -ForegroundColor Cyan
Start-Process "http://localhost:8085/stark-security"
Start-Sleep -Seconds 2
Start-Process "http://localhost:8761"
$ROOT = "C:\Users\andre\Documents\GitHub\StarkDistribuidos"
$MAVEN = "$ROOT\mvnw.cmd"

Write-Host "🚀 INICIANDO TODOS LOS SERVICIOS..." -ForegroundColor Cyan

# 1. EUREKA SERVER (8761)
Write-Host "📍 Iniciando Eureka Server en puerto 8761..." -ForegroundColor Yellow
Start-Process -WindowStyle Normal -FilePath "cmd.exe" -ArgumentList "/k `"cd $ROOT\starkDistribuidos-config && $MAVEN spring-boot:run`"" -NoNewWindow:$false

# 2. API GATEWAY (8080)
Write-Host "⏳ Esperando 20 segundos para Eureka..." -ForegroundColor Gray
Start-Sleep -Seconds 20
Write-Host "📍 Iniciando API Gateway en puerto 8080..." -ForegroundColor Yellow
Start-Process -WindowStyle Normal -FilePath "cmd.exe" -ArgumentList "/k `"cd $ROOT\starkDistribuidos-gateway && $MAVEN spring-boot:run`"" -NoNewWindow:$false

# 3. AUTH SERVICE (8081)
Write-Host "⏳ Esperando 15 segundos para Gateway..." -ForegroundColor Gray
Start-Sleep -Seconds 15
Write-Host "📍 Iniciando Auth Service en puerto 8081..." -ForegroundColor Yellow
Start-Process -WindowStyle Normal -FilePath "cmd.exe" -ArgumentList "/k `"cd $ROOT\starkDistribuidos-auth && $MAVEN spring-boot:run`"" -NoNewWindow:$false

# 4. SENSOR SERVICE (8081)
Write-Host "⏳ Esperando 15 segundos..." -ForegroundColor Gray
Start-Sleep -Seconds 15
Write-Host "📍 Iniciando Sensor Service en puerto 8081..." -ForegroundColor Yellow
Start-Process -WindowStyle Normal -FilePath "cmd.exe" -ArgumentList "/k `"cd $ROOT\starkDistribuidos-sensor && $MAVEN spring-boot:run`"" -NoNewWindow:$false

# 5. ALERT SERVICE (8083)
Write-Host "⏳ Esperando 15 segundos..." -ForegroundColor Gray
Start-Sleep -Seconds 15
Write-Host "📍 Iniciando Alert Service en puerto 8083..." -ForegroundColor Yellow
Start-Process -WindowStyle Normal -FilePath "cmd.exe" -ArgumentList "/k `"cd $ROOT\starkDistribuidos-alert && $MAVEN spring-boot:run`"" -NoNewWindow:$false

# 6. NOTIFICATION SERVICE (8085)
Write-Host "⏳ Esperando 15 segundos..." -ForegroundColor Gray
Start-Sleep -Seconds 15
Write-Host "📍 Iniciando Notification Service en puerto 8085..." -ForegroundColor Yellow
Start-Process -WindowStyle Normal -FilePath "cmd.exe" -ArgumentList "/k `"cd $ROOT\starkDistribuidos-notification && $MAVEN spring-boot:run`"" -NoNewWindow:$false

# 7. FRONTEND (8086)
Write-Host "⏳ Esperando 15 segundos..." -ForegroundColor Gray
Start-Sleep -Seconds 15
Write-Host "📍 Iniciando Frontend en puerto 8086..." -ForegroundColor Yellow
Start-Process -WindowStyle Normal -FilePath "cmd.exe" -ArgumentList "/k `"cd $ROOT\starkDistribuidos-frontend && $MAVEN spring-boot:run`"" -NoNewWindow:$false

Write-Host ""
Write-Host "✅ TODOS LOS SERVICIOS SE HAN LANZADO" -ForegroundColor Green
Write-Host ""
Write-Host "📊 ESPERANDO A QUE INICIEN (90-120 SEGUNDOS)..." -ForegroundColor Cyan
Write-Host ""
Write-Host "Puertos activos:" -ForegroundColor Yellow
Write-Host "  📍 Eureka Server      → http://localhost:8761" -ForegroundColor White
Write-Host "  📍 API Gateway        → http://localhost:8080" -ForegroundColor White
Write-Host "  📍 Auth Service       → http://localhost:8081" -ForegroundColor White
Write-Host "  📍 Sensor Service     → http://localhost:8081" -ForegroundColor White
Write-Host "  📍 Alert Service      → http://localhost:8083" -ForegroundColor White
Write-Host "  📍 Notification Srv   → http://localhost:8085" -ForegroundColor White
Write-Host "  📍 Frontend           → http://localhost:8086" -ForegroundColor White
Write-Host ""
Write-Host "Espera 2 minutos y luego accede a Eureka para verificar que todos estén UP" -ForegroundColor Cyan


# 🚀 SCRIPT DE INICIO COMPLETO - STARK INDUSTRIES SECURITY SYSTEM
# Este script inicia todos los microservicios en orden correcto

Write-Host "╔═══════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   STARK INDUSTRIES - SISTEMA DE SEGURIDAD DISTRIBUIDO          ║" -ForegroundColor Cyan
Write-Host "║                  INICIANDO SERVICIOS...                        ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

$projectRoot = "C:\Users\andre\Documents\GitHub\StarkDistribuidos"
$services = @(
    @{
        Name = "EUREKA SERVER"
        Dir = "starkDistribuidos-config"
        Port = "8761"
        Color = "Yellow"
    },
    @{
        Name = "API GATEWAY"
        Dir = "starkDistribuidos-gateway"
        Port = "8080"
        Color = "Cyan"
    },
    @{
        Name = "SENSOR SERVICE"
        Dir = "starkDistribuidos-sensor"
        Port = "8081"
        Color = "Green"
    },
    @{
        Name = "ALERT SERVICE"
        Dir = "starkDistribuidos-alert"
        Port = "8083"
        Color = "Magenta"
    },
    @{
        Name = "NOTIFICATION SERVICE"
        Dir = "starkDistribuidos-notification"
        Port = "8085"
        Color = "Red"
    },
    @{
        Name = "FRONTEND"
        Dir = "starkDistribuidos-frontend"
        Port = "8082"
        Color = "Blue"
    }
)

$processes = @()

foreach ($service in $services) {
    Write-Host "┌─ Iniciando: $($service.Name) ($($service.Port))" -ForegroundColor $service.Color

    $servicePath = Join-Path $projectRoot $service.Dir

    # Crear una nueva ventana para cada servicio
    $process = Start-Process -FilePath "cmd.exe" `
        -ArgumentList "/k cd $servicePath ; ..\mvnw.cmd spring-boot:run" `
        -WindowStyle Normal `
        -PassThru

    $processes += @{
        Name = $service.Name
        ProcessId = $process.Id
        Port = $service.Port
    }

    Write-Host "│  ✓ PID: $($process.Id)" -ForegroundColor $service.Color
    Write-Host "└─ Esperando 15 segundos antes del siguiente servicio..." -ForegroundColor $service.Color

    Start-Sleep -Seconds 15
}

Write-Host ""
Write-Host "╔═══════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║                   ✅ TODOS LOS SERVICIOS INICIADOS             ║" -ForegroundColor Green
Write-Host "╚═══════════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""

Write-Host "📊 PANEL DE CONTROL:" -ForegroundColor Cyan
Write-Host "  Eureka Dashboard:   http://localhost:8761" -ForegroundColor Yellow
Write-Host "  API Gateway:        http://localhost:8080" -ForegroundColor Cyan
Write-Host "  Frontend Web:       http://localhost:8082" -ForegroundColor Blue
Write-Host ""

Write-Host "🔗 ENDPOINTS DE PRUEBA:" -ForegroundColor Green
Write-Host "  Health Check:       curl http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host "  Sensores:           curl http://localhost:8081/api/sensors" -ForegroundColor White
Write-Host "  Alertas:            curl http://localhost:8083/api/alerts" -ForegroundColor White
Write-Host "  Notificaciones:     curl http://localhost:8085/api/notifications" -ForegroundColor White
Write-Host ""

Write-Host "📋 PROCESOS INICIADOS:" -ForegroundColor Cyan
foreach ($proc in $processes) {
    Write-Host "  [$($proc.ProcessId)] $($proc.Name) (Puerto: $($proc.Port))" -ForegroundColor Green
}

Write-Host ""
Write-Host "⏹️  Para detener los servicios, cierra las ventanas o ejecuta:" -ForegroundColor Yellow
Write-Host "  Stop-Process -Id <PID> -Force" -ForegroundColor White
Write-Host ""
Write-Host "Presiona ENTER para salir (los servicios continuarán ejecutándose)..."
Read-Host



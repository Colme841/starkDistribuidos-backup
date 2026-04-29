@echo off
setlocal enabledelayedexpansion

set ROOT=C:\Users\andre\Documents\GitHub\StarkDistribuidos

echo.
echo ============================================
echo  STARK INDUSTRIES - SISTEMA DISTRIBUIDO
echo ============================================
echo.
echo Lanzando todos los servicios...
echo.

REM Kill any existing Java processes
taskkill /F /IM java.exe 2>nul

echo ========================================
echo 1. EUREKA SERVER (8761)
echo ========================================
start "EUREKA-8761" cmd /k "cd %ROOT%\starkDistribuidos-config && %ROOT%\mvnw.cmd spring-boot:run"
timeout /t 20 /nobreak

echo.
echo ========================================
echo 2. API GATEWAY (8080)
echo ========================================
start "GATEWAY-8080" cmd /k "cd %ROOT%\starkDistribuidos-gateway && %ROOT%\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

echo.
echo ========================================
echo 3. AUTH SERVICE (8081)
echo ========================================
start "AUTH-8081" cmd /k "cd %ROOT%\starkDistribuidos-auth && %ROOT%\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

echo.
echo ========================================
echo 4. SENSOR SERVICE (8081)
echo ========================================
start "SENSOR-8081" cmd /k "cd %ROOT%\starkDistribuidos-sensor && %ROOT%\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

echo.
echo ========================================
echo 5. ALERT SERVICE (8083)
echo ========================================
start "ALERT-8083" cmd /k "cd %ROOT%\starkDistribuidos-alert && %ROOT%\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

echo.
echo ========================================
echo 6. NOTIFICATION SERVICE (8085)
echo ========================================
start "NOTIFICATION-8085" cmd /k "cd %ROOT%\starkDistribuidos-notification && %ROOT%\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

echo.
echo ========================================
echo 7. FRONTEND (8086)
echo ========================================
start "FRONTEND-8086" cmd /k "cd %ROOT%\starkDistribuidos-frontend && %ROOT%\mvnw.cmd spring-boot:run"

echo.
echo ============================================
echo  TODOS LOS SERVICIOS HAN SIDO LANZADOS
echo ============================================
echo.
echo Esperando a que se estabilicen... (90-120 segundos)
echo.
echo URLs de acceso:
echo   - Eureka:      http://localhost:8761 (admin/admin123)
echo   - Gateway:     http://localhost:8080
echo   - Frontend:    http://localhost:8086
echo.
echo Revisa el Eureka Dashboard en 2 minutos para verificar
echo que todos los servicios estén en estado UP
echo.
pause


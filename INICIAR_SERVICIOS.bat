@echo off
REM Script para iniciar todos los servicios de Stark Industries

setlocal enabledelayedexpansion

echo.
echo =====================================================
echo    STARK INDUSTRIES - Sistema de Seguridad Distribuido
echo =====================================================
echo.

REM Obtener rutas
set "ROOT_DIR=%~dp0"
set "EUREKA_DIR=%ROOT_DIR%starkDistribuidos-gateway"
set "FRONTEND_DIR=%ROOT_DIR%starkDistribuidos-frontend"

REM Matar procesos Java previos
echo [1/3] Deteniendo procesos Java previos...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 /nobreak >nul

REM Iniciar Eureka Gateway
echo.
echo [2/3] Iniciando Eureka Server (Gateway) en puerto 8080...
echo Ubicación: %EUREKA_DIR%
cd /d "%EUREKA_DIR%"
start "Eureka Server" cmd /k "mvn spring-boot:run -DskipTests"
timeout /t 10 /nobreak >nul

REM Iniciar Frontend
echo.
echo [3/3] Iniciando Frontend en puerto 8085...
echo Ubicación: %FRONTEND_DIR%
cd /d "%FRONTEND_DIR%"
start "Stark Frontend" cmd /k "mvn spring-boot:run -DskipTests"

echo.
echo =====================================================
echo Servicios iniciados:
echo  - Eureka Server:    http://localhost:8080
echo  - Frontend:         http://localhost:8085/stark-security
echo.
echo Credenciales de prueba:
echo  - Usuario:  admin
echo  - Clave:    admin123
echo =====================================================
echo.
pause


@echo off
REM ============================================================
REM   STARK INDUSTRIES - INICIADOR DE SERVICIOS
REM   Script para lanzar todos los microservicios
REM ============================================================

setlocal enabledelayedexpansion
color 0A

echo.
echo ============================================================
echo    STARK INDUSTRIES - SISTEMA DE SEGURIDAD DISTRIBUIDO
echo    Iniciando Microservicios...
echo ============================================================
echo.

set ROOT=C:\Users\andre\Documents\GitHub\StarkDistribuidos

REM 1. EUREKA SERVER
echo [1/6] Iniciando Eureka Server (Puerto 8761)...
start "Eureka Server 8761" cmd /k "cd %ROOT%\starkDistribuidos-config && ..\mvnw.cmd spring-boot:run"
timeout /t 20 /nobreak

REM 2. GATEWAY
echo [2/6] Iniciando API Gateway (Puerto 8080)...
start "API Gateway 8080" cmd /k "cd %ROOT%\starkDistribuidos-gateway && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 3. SENSOR SERVICE
echo [3/6] Iniciando Sensor Service (Puerto 8081)...
start "Sensor Service 8081" cmd /k "cd %ROOT%\starkDistribuidos-sensor && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 4. ALERT SERVICE
echo [4/6] Iniciando Alert Service (Puerto 8083)...
start "Alert Service 8083" cmd /k "cd %ROOT%\starkDistribuidos-alert && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 5. NOTIFICATION SERVICE
echo [5/6] Iniciando Notification Service (Puerto 8085)...
start "Notification Service 8085" cmd /k "cd %ROOT%\starkDistribuidos-notification && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 6. FRONTEND
echo [6/6] Iniciando Frontend (Puerto 8086)...
start "Frontend 8086" cmd /k "cd %ROOT%\starkDistribuidos-frontend && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

echo.
echo ============================================================
echo    EXITO - TODOS LOS SERVICIOS HAN SIDO INICIADOS
echo ============================================================
echo.
echo UBICACION DE SERVICIOS:
echo   - Frontend Web ............... http://localhost:8086
echo   - Eureka Dashboard ........... http://localhost:8761
echo   - API Gateway ................ http://localhost:8080
echo.
echo SERVICIOS INDIVIDUALES:
echo   - Sensor Service ............. http://localhost:8081
echo   - Alert Service .............. http://localhost:8083
echo   - Notification Service ....... http://localhost:8085
echo.
echo Presiona cualquier tecla para salir de este script...
echo (Los servicios continuaran ejecutandose en sus ventanas)
pause




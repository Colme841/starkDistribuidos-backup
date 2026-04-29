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
echo [1/9] Iniciando Eureka Server (Puerto 8761)...
start "Eureka Server 8761" cmd /k "cd %ROOT%\starkDistribuidos-config && ..\mvnw.cmd spring-boot:run"
timeout /t 20 /nobreak

REM 2. GATEWAY
echo [2/9] Iniciando API Gateway (Puerto 8080)...
start "API Gateway 8080" cmd /k "cd %ROOT%\starkDistribuidos-gateway && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 3. SENSOR SERVICE
echo [3/9] Iniciando Sensor Service (Puerto 8082)...
start "Sensor Service 8082" cmd /k "cd %ROOT%\starkDistribuidos-sensor && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 4. SENSOR MOVIMIENTO
echo [4/9] Iniciando Sensor Movimiento (Puerto 8091)...
start "Sensor Movimiento 8091" cmd /k "cd %ROOT%\starkDistribuidos-sensor-movimiento && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 5. SENSOR TEMPERATURA
echo [5/9] Iniciando Sensor Temperatura (Puerto 8092)...
start "Sensor Temperatura 8092" cmd /k "cd %ROOT%\starkDistribuidos-sensor-temperatura && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 6. SENSOR ACCESO
echo [6/9] Iniciando Sensor Acceso (Puerto 8093)...
start "Sensor Acceso 8093" cmd /k "cd %ROOT%\starkDistribuidos-sensor-acceso && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 7. ALERT SERVICE
echo [7/9] Iniciando Alert Service (Puerto 8083)...
start "Alert Service 8083" cmd /k "cd %ROOT%\starkDistribuidos-alert && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 8. NOTIFICATION SERVICE
echo [8/9] Iniciando Notification Service (Puerto 8085)...
start "Notification Service 8085" cmd /k "cd %ROOT%\starkDistribuidos-notification && ..\mvnw.cmd spring-boot:run"
timeout /t 15 /nobreak

REM 9. FRONTEND
echo [9/9] Iniciando Frontend (Puerto 8086)...
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
echo   - Sensor Service ............. http://localhost:8082
echo   - Sensor Movimiento .......... http://localhost:8091
echo   - Sensor Temperatura ......... http://localhost:8092
echo   - Sensor Acceso .............. http://localhost:8093
echo   - Alert Service .............. http://localhost:8083
echo   - Notification Service ....... http://localhost:8085
echo.
echo Presiona cualquier tecla para salir de este script...
echo (Los servicios continuaran ejecutandose en sus ventanas)
pause

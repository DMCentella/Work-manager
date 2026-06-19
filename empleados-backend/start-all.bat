@echo off
setlocal enabledelayedexpansion

:: ------------------------------------------------------------------
:: start-all.bat — Arranca todos los servicios en orden correcto
::
:: Orden:
::   1. common-lib          (build e install en repo local)
::   2. discovery-service   (Eureka) — espera hasta que esté listo
::   3. Microservicios      — auth, empleado, asistencia, tarea
::   4. gateway-service     — último
::
:: Requisitos: JDK 17+, Maven en PATH, MySQL corriendo en :3306
:: ------------------------------------------------------------------

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

:: --- Detectar JAVA_HOME si no está definido ---
if defined JAVA_HOME goto :maven_check
for /f "tokens=*" %%i in ('where java 2^>nul') do (
    for %%j in ("%%i") do set "JAVA_BIN=%%~dpj"
    if exist "!JAVA_BIN!\javac.exe" (
        for %%j in ("!JAVA_BIN!.") do set "JAVA_HOME=%%~dpj"
        goto :found_java
    )
)
echo [ERROR] No se encontro JAVA_HOME. Define la variable o instala JDK 17.
pause
exit /b 1

:found_java
echo JAVA_HOME detectado: %JAVA_HOME%

:: --- Verificar Maven ---
:maven_check
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven (mvn) no encontrado en el PATH.
    pause
    exit /b 1
)
echo Maven detectado.

:: --- Verificar MySQL (opcional, solo avisa) ---
echo Verifica que MySQL este corriendo en localhost:3306 (root/123456).
echo.

:: ================================================================
:: PASO 0: Instalar common-lib
:: ================================================================
echo ==========================================
echo    Instalando common-lib en repo local
echo ==========================================
cd /d "%SCRIPT_DIR%common-lib"
call mvn clean install -DskipTests -q
if %errorlevel% neq 0 (
    echo [ERROR] Fallo la instalacion de common-lib.
    pause
    exit /b 1
)
echo [OK] common-lib instalado.
echo.

:: ================================================================
:: PASO 1: Discovery Service (Eureka)
:: ================================================================
echo ==========================================
echo    Iniciando discovery-service (Eureka)
echo    Puerto 8761
echo ==========================================
cd /d "%SCRIPT_DIR%discovery-service"
start "discovery-service" cmd /c "mvn spring-boot:run -q"

:: Esperar hasta que Eureka responda
echo Esperando discovery-service (puerto 8761)...
set "tries=0"
:wait_discovery
timeout /t 3 /nobreak >nul
set /a tries+=1
curl -s http://localhost:8761 >nul 2>&1
if %errorlevel% equ 0 goto :discovery_ready
if %tries% geq 40 (
    echo [ERROR] Timeout esperando discovery-service.
    pause
    exit /b 1
)
goto :wait_discovery

:discovery_ready
echo [OK] discovery-service listo (8761).
echo.

:: ================================================================
:: PASO 2: Microservicios
:: ================================================================
echo ==========================================
echo    Iniciando microservicios
echo    auth(8081) empleado(8082)
echo    asistencia(8083) tarea(8084)
echo ==========================================

cd /d "%SCRIPT_DIR%auth-service"
start "auth-service"       cmd /c "mvn spring-boot:run -q"

cd /d "%SCRIPT_DIR%empleado-service"
start "empleado-service"   cmd /c "mvn spring-boot:run -q"

cd /d "%SCRIPT_DIR%asistencia-service"
start "asistencia-service" cmd /c "mvn spring-boot:run -q"

cd /d "%SCRIPT_DIR%tarea-service"
start "tarea-service"      cmd /c "mvn spring-boot:run -q"

:: Dar tiempo para que arranquen
echo Esperando microservicios (30s)...
timeout /t 30 /nobreak >nul
echo [OK] Microservicios lanzados.
echo.

:: ================================================================
:: PASO 3: Notification Service
:: ================================================================
echo ==========================================
echo    Iniciando notification-service
echo    Puerto 8085
echo ==========================================
cd /d "%SCRIPT_DIR%notification-service"
start "notification-service" cmd /c "mvn spring-boot:run -q"
echo [OK] notification-service lanzado.
echo.

:: ================================================================
:: PASO 4: Gateway Service
:: ================================================================
echo ==========================================
echo    Iniciando gateway-service
echo    Puerto 8080
echo ==========================================
cd /d "%SCRIPT_DIR%gateway-service"
start "gateway-service" cmd /c "mvn spring-boot:run -q"

echo Esperando gateway-service (puerto 8080)...
set "tries=0"
:wait_gateway
timeout /t 3 /nobreak >nul
set /a tries+=1
curl -s http://localhost:8080 >nul 2>&1
if %errorlevel% equ 0 goto :gateway_ready
if %tries% geq 20 (
    echo [AVISO] Gateway no respondio en 60s. Puede seguir cargando.
    goto :done
)
goto :wait_gateway

:gateway_ready
echo [OK] gateway-service listo (8080).

:: ================================================================
:done
echo.
echo ==========================================
echo    TODOS LOS SERVICIOS INICIADOS
echo ==========================================
echo  Gateway      http://localhost:8080
echo  Discovery    http://localhost:8761
echo  Auth         http://localhost:8081
echo  Empleados    http://localhost:8082
echo  Asistencias  http://localhost:8083
echo  Tareas       http://localhost:8084
echo  Notificaciones http://localhost:8085
echo ==========================================
echo  Admin: admin / 12345
echo ==========================================
echo.
echo Cierra las ventanas individuales para detener cada servicio.
echo.

cd /d "%SCRIPT_DIR%"
pause

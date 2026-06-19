# ------------------------------------------------------------------
# start-all.ps1 — Arranca todos los servicios en orden correcto
#
# Orden:
#   1. common-lib          (build e install en repo local)
#   2. discovery-service   (Eureka) — espera hasta que esté listo
#   3. Microservicios      — auth, empleado, asistencia, tarea
#   4. gateway-service     — último
#
# Requisitos: JDK 17+, Maven en PATH, MySQL corriendo en :3306
# ------------------------------------------------------------------

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Push-Location $ScriptDir

# --- Colores para output ---
function Write-Step($msg) {
    Write-Host "`n==========================================" -ForegroundColor Cyan
    Write-Host "   $msg" -ForegroundColor Cyan
    Write-Host "==========================================" -ForegroundColor Cyan
}

function Write-Ok($msg)  { Write-Host "[OK] $msg" -ForegroundColor Green }
function Write-Err($msg) { Write-Host "[ERROR] $msg" -ForegroundColor Red }

# --- Verificar prerequisitos ---
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Err "Maven (mvn) no encontrado en el PATH."
    pause; Pop-Location; exit 1
}

if ($env:JAVA_HOME) {
    Write-Ok "JAVA_HOME: $env:JAVA_HOME"
} else {
    Write-Host "[AVISO] JAVA_HOME no definido. Se usara java del PATH." -ForegroundColor Yellow
}

Write-Host "[INFO] Verifica que MySQL este corriendo en localhost:3306 (root/123456).`n"

# ================================================================
# PASO 0: Instalar common-lib
# ================================================================
Write-Step "Instalando common-lib en repo local"
Push-Location "$ScriptDir\common-lib"
mvn clean install -DskipTests -q
if ($LASTEXITCODE -ne 0) { Write-Err "Fallo common-lib."; Pop-Location; pause; exit 1 }
Pop-Location
Write-Ok "common-lib instalado"

# ================================================================
# PASO 1: Discovery Service (Eureka)
# ================================================================
Write-Step "Iniciando discovery-service (Eureka) - Puerto 8761"

$discoveryJob = Start-Job -Name "discovery-service" -ScriptBlock {
    param($dir)
    Set-Location $dir
    mvn spring-boot:run -q 2>&1 | Out-Null
} -ArgumentList "$ScriptDir\discovery-service"

# Esperar hasta que Eureka responda
Write-Host "Esperando discovery-service (puerto 8761)..."
$ready = $false
for ($i = 1; $i -le 40; $i++) {
    try {
        $null = Invoke-WebRequest -Uri "http://localhost:8761" -TimeoutSec 2 -UseBasicParsing
        $ready = $true
        break
    } catch {}
    Start-Sleep -Seconds 3
}
if (-not $ready) {
    Write-Err "Timeout esperando discovery-service."
    Stop-Job -Name "discovery-service"
    Remove-Job -Name "discovery-service"
    pause; Pop-Location; exit 1
}
Write-Ok "discovery-service listo (8761)"

# ================================================================
# PASO 2: Microservicios en paralelo
# ================================================================
Write-Step "Iniciando microservicios (auth 8081, empleado 8082, asistencia 8083, tarea 8084)"

$microJobs = @()

$microJobs += Start-Job -Name "auth-service" -ScriptBlock {
    param($d) Set-Location $d; mvn spring-boot:run -q
} -ArgumentList "$ScriptDir\auth-service"

$microJobs += Start-Job -Name "empleado-service" -ScriptBlock {
    param($d) Set-Location $d; mvn spring-boot:run -q
} -ArgumentList "$ScriptDir\empleado-service"

$microJobs += Start-Job -Name "asistencia-service" -ScriptBlock {
    param($d) Set-Location $d; mvn spring-boot:run -q
} -ArgumentList "$ScriptDir\asistencia-service"

$microJobs += Start-Job -Name "tarea-service" -ScriptBlock {
    param($d) Set-Location $d; mvn spring-boot:run -q
} -ArgumentList "$ScriptDir\tarea-service"

Write-Host "Esperando que los microservicios esten listos (30s)..."
Start-Sleep -Seconds 30
Write-Ok "Microservicios lanzados"

# ================================================================
# PASO 3: Notification Service
# ================================================================
Write-Step "Iniciando notification-service - Puerto 8085"

$notifJob = Start-Job -Name "notification-service" -ScriptBlock {
    param($dir)
    Set-Location $dir
    mvn spring-boot:run -q 2>&1 | Out-Null
} -ArgumentList "$ScriptDir\notification-service"

Start-Sleep -Seconds 10
Write-Ok "notification-service lanzado"

# ================================================================
# PASO 4: Gateway Service
# ================================================================
Write-Step "Iniciando gateway-service - Puerto 8080"

$gatewayJob = Start-Job -Name "gateway-service" -ScriptBlock {
    param($dir)
    Set-Location $dir
    mvn spring-boot:run -q 2>&1 | Out-Null
} -ArgumentList "$ScriptDir\gateway-service"

# Esperar gateway
Write-Host "Esperando gateway-service (puerto 8080)..."
$ready = $false
for ($i = 1; $i -le 20; $i++) {
    try {
        $null = Invoke-WebRequest -Uri "http://localhost:8080" -TimeoutSec 2 -UseBasicParsing
        $ready = $true
        break
    } catch {}
    Start-Sleep -Seconds 3
}
if ($ready) { Write-Ok "gateway-service listo (8080)" } else { Write-Host "[AVISO] Gateway no respondio - puede seguir cargando." -ForegroundColor Yellow }

# ================================================================
# Resumen
# ================================================================
Write-Host "`n==========================================" -ForegroundColor Green
Write-Host "    TODOS LOS SERVICIOS INICIADOS"            -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host " Gateway:      http://localhost:8080"
Write-Host " Discovery:    http://localhost:8761"
Write-Host " Auth:         http://localhost:8081"
Write-Host " Empleados:    http://localhost:8082"
Write-Host " Asistencias:  http://localhost:8083"
Write-Host " Tareas:       http://localhost:8084"
Write-Host " Notificaciones: http://localhost:8085"
Write-Host "=========================================="
Write-Host " Admin: admin / 12345"
Write-Host "==========================================`n"
Write-Host "Presiona Ctrl+C para detener todos los servicios." -ForegroundColor Yellow
Write-Host ""

# Mantener vivo y manejar Ctrl+C para limpiar
$ctrlCPressed = $false
[Console]::TreatControlCAsInput = $false
try {
    while ($true) {
        Start-Sleep -Seconds 1
    }
} finally {
    Write-Host "`nDeteniendo todos los servicios..." -ForegroundColor Yellow
    Get-Job | Stop-Job
    Get-Job | Remove-Job
    # Matar cualquier proceso Maven rezagado
    Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
        $_.CommandLine -like "*spring-boot*" -or $_.CommandLine -like "*maven*"
    } | Stop-Process -Force -ErrorAction SilentlyContinue
    Write-Host "Listo." -ForegroundColor Green
    Pop-Location
}

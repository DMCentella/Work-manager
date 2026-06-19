#!/usr/bin/env bash
# ------------------------------------------------------------------
# start-all.sh — Arranca todos los servicios en orden correcto.
#
# Orden:
#   1. common-lib   (build e install en repo local)
#   2. discovery-service (Eureka) — espera hasta que esté listo
#   3. Microservicios       — auth, empleado, asistencia, tarea (en paralelo)
#   4. gateway-service      — último, después de los microservicios
#
# Uso:  ./start-all.sh              (logs en consola mezclados)
#       ./start-all.sh --logs       (un log por servicio en logs/)
# ------------------------------------------------------------------

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$SCRIPT_DIR/logs"
PID_DIR="$SCRIPT_DIR/.pids"
USE_SEPARATE_LOGS=false
PORT_CHECK_TIMEOUT=120
POLL_INTERVAL=2

# --- Argumentos ---
if [ "$1" = "--logs" ]; then
    USE_SEPARATE_LOGS=true
    mkdir -p "$LOG_DIR"
fi

mkdir -p "$PID_DIR"

cleanup() {
    echo ""
    echo "🛑 Deteniendo todos los servicios..."
    if [ -d "$PID_DIR" ]; then
        for pidfile in "$PID_DIR"/*.pid; do
            [ -f "$pidfile" ] || continue
            local pid
            pid=$(cat "$pidfile")
            local svc
            svc=$(basename "$pidfile" .pid)
            if kill -0 "$pid" 2>/dev/null; then
                kill "$pid" 2>/dev/null || true
                echo "   ✔ $svc (PID $pid) detenido"
            fi
            rm -f "$pidfile"
        done
    fi
    # Matar procesos mvn rezagados
    pkill -f "spring-boot:run" 2>/dev/null || true
    echo "✅ Listo."
}
trap cleanup EXIT INT TERM

# ------ helpers ------

wait_for_port() {
    local host=${1:-localhost}
    local port=$2
    local timeout=${3:-$PORT_CHECK_TIMEOUT}
    local elapsed=0
    echo "   ⏳ Esperando $host:$port ..."
    while ! (echo > "/dev/tcp/$host/$port") 2>/dev/null; do
        sleep "$POLL_INTERVAL"
        elapsed=$((elapsed + POLL_INTERVAL))
        if [ "$elapsed" -ge "$timeout" ]; then
            echo "   ❌ Timeout esperando $host:$port"
            return 1
        fi
    done
    echo "   ✔ $host:$port listo (${elapsed}s)"
}

start_service() {
    local dir=$1
    local port=$2
    local name=$3
    local depend_on_port=$4   # opcional: esperar este puerto antes de arrancar

    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "▶  Iniciando $name (puerto $port)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    if [ -n "$depend_on_port" ]; then
        wait_for_port localhost "$depend_on_port" || return 1
    fi

    if $USE_SEPARATE_LOGS; then
        ( cd "$SCRIPT_DIR/$dir" && mvn -q spring-boot:run > "$LOG_DIR/$name.log" 2>&1 ) &
    else
        ( cd "$SCRIPT_DIR/$dir" && mvn -q spring-boot:run 2>&1 | sed "s/^/[$name] /" ) &
    fi
    local pid=$!
    echo "$pid" > "$PID_DIR/$name.pid"
    echo "   PID: $pid"

    wait_for_port localhost "$port"
}

# =============== MAIN ===============

echo "╔══════════════════════════════════════════╗"
echo "║   ARRANQUE DE MICROSERVICIOS            ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# --- Paso 0: common-lib ---
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📦  Instalando common-lib en repo local"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
cd "$SCRIPT_DIR/common-lib"
mvn clean install -DskipTests -q
echo "   ✔ common-lib instalado"

# --- Paso 1: Discovery (Eureka) ---
start_service "discovery-service" 8761 "discovery-service"

# --- Paso 2: Microservicios en paralelo ---
# Cada uno depende de discovery (8761)
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "▶  Iniciando microservicios (auth, empleado, asistencia, tarea)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

start_service "auth-service"      8081 "auth-service"      8761 &
start_service "empleado-service" 8082 "empleado-service"  8761 &
start_service "asistencia-service" 8083 "asistencia-service" 8761 &
start_service "tarea-service"    8084 "tarea-service"     8761 &

wait  # esperar a que los 4 terminen (es decir, que sus puertos estén abiertos)

# --- Paso 3: Notification ---
start_service "notification-service" 8085 "notification-service" 8761

# --- Paso 4: Gateway (último) ---
start_service "gateway-service" 8080 "gateway-service"

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║ ✅  Todos los servicios en ejecución    ║"
echo "╠══════════════════════════════════════════╣"
echo "║  Gateway:      http://localhost:8080     ║"
echo "║  Discovery:    http://localhost:8761     ║"
echo "║  Auth:         http://localhost:8081     ║"
echo "║  Empleados:    http://localhost:8082     ║"
echo "║  Asistencias:  http://localhost:8083     ║"
echo "║  Tareas:       http://localhost:8084     ║"
echo "║  Notificaciones: http://localhost:8085   ║"
echo "╠══════════════════════════════════════════╣"
echo "║  Presiona Ctrl+C para detener todo      ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# Mantener el script vivo mientras los servicios corren
wait

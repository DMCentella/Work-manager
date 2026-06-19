# AGENTS.md — empleados-backend

## Stack
- **Java 17**, **Spring Boot 3.4.3**, **Spring Cloud 2024.0.1**, **Maven** (no wrapper — `mvn` must be on PATH)
- **MySQL 8** on `localhost:3306` with user `root` / `123456`
- JWT auth via `common-lib` (`io.jsonwebtoken:jjwt-*:0.11.5`)
- **Lombok 1.18.36** — all entities and DTOs use `@Data` / `@NoArgsConstructor`
- **MapStruct 1.6.3** — entity ↔ DTO mapping in all data services (empleado, asistencia, tarea)
- **RabbitMQ** — async events between services (tarea, asistencia, empleado → notification)
- **WebSocket** — real-time notifications via STOMP (notification-service, `/ws`)
- PDF export: `openpdf 1.3.26`, Excel: `poi-ooxml 4.1.0` (empleado-service only)

## Module map

| Directory | Artifact | Port | DB | Role |
|---|---|---|---|---|
| `common-lib/` | `common-lib` | — | — | Shared JWT util (`JwtUtil`) |
| `discovery-service/` | `discovery-service` | 8761 | — | Eureka registry |
| `gateway-service/` | `gateway-service` | 8080 | — | Spring Cloud Gateway + JWT filter |
| `auth-service/` | `auth-service` | 8081 | `auth_db` | Login, register, validate |
| `empleado-service/` | `empleado-service` | 8082 | `empleados_db` | Empleados CRUD + PDF export |
| `asistencia-service/` | `asistencia-service` | 8083 | `asistencias_db` | Asistencias CRUD + PDF export |
| `tarea-service/` | `tarea-service` | 8084 | `tareas_db` | Tareas CRUD |
| `notification-service/` | `notification-service` | 8085 | `notificaciones_db` | Notificaciones WS + RabbitMQ |

Each service has its own `pom.xml`. There is **no parent/aggregator POM** at the root.

## Build gotchas

- **`common-lib` must be installed first** — every other service depends on it:
  ```
  cd common-lib && mvn clean install -DskipTests
  ```
- Each service builds independently from its own directory:
  ```
  cd empleado-service && mvn clean package -DskipTests
  ```
- Spring Boot Maven plugin is present (`mvn spring-boot:run` works) but no packaging type is explicitly set (defaults to jar).
- **MapStruct requires annotation processor config** in `pom.xml` (lombok + mapstruct-processor + lombok-mapstruct-binding). This is already configured in empleado/asistencia/tarea services. If adding a new data service, copy the build plugin config.

## Running

### Prerequisites
- MySQL 8 running on `localhost:3306`, user `root` password `123456`
- The 4 databases (`auth_db`, `empleados_db`, `asistencias_db`, `tareas_db`) are auto-created by Hibernate (`ddl-auto=update`)

### Startup order (strict)
1. `discovery-service` — Eureka must be up before anything else registers
2. `gateway-service`, `auth-service`, `empleado-service`, `asistencia-service`, `tarea-service` (in any order after discovery)

Each service logs its port. Gateway routes are pre-configured in `application.properties`.

### Quick run

**Windows:** `start-all.ps1` (PowerShell) o `start-all.bat` (CMD) — abre servicios.

**WSL/Linux:** `./start-all.sh` — logs consolidados, Ctrl+C detiene todo.

Manual (one terminal per service):
```bash
# Build common-lib once
cd common-lib && mvn clean install -DskipTests && cd ..

# Start each in a separate terminal
cd discovery-service && mvn spring-boot:run
cd gateway-service && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
cd empleado-service && mvn spring-boot:run
cd asistencia-service && mvn spring-boot:run
cd tarea-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

## JWT behavior

- The signing key is generated **at JVM startup** via `Keys.secretKeyFor(SignatureAlgorithm.HS256)` — **all tokens become invalid on any restart** of auth-service.
- Gateway validates tokens and forwards `X-Username`, `X-Roles`, `X-UserId`, `X-EmpleadoId` headers to downstream services.
- Unauthenticated paths: `POST /api/auth/login`, `POST /api/auth/register`. Everything else requires `Authorization: Bearer <token>`.
- Admin user `admin` / `12345` (ROLE_ADMIN) is created automatically by `AuthServiceApplication.initAdmin()`.

## Authorization model

- `ROLE_ADMIN` — full CRUD on all resources, PDF exports
- `ROLE_USER` — own panel, own tasks, own asistencias
- Role enforcement lives in downstream services (not the gateway)

## Testing

- **No tests exist in any module.** `-DskipTests` is implicit for now; the flag is included above for when tests are added.
- Modules lack `spring-boot-starter-test` in their POMs; add it before writing tests.

## Logging & debug

- `spring.jpa.show-sql=true` and `hibernate.format_sql=true` is set on all data services — SQL is logged to stdout.
- Common startup issues: Eureka not up before other services, MySQL connection refused, `common-lib` not installed into local Maven repo.

## API docs

Full endpoint reference in `API.md`. Gateway base URL: `http://localhost:8080`.

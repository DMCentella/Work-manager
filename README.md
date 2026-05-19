# 🚨 Alerta Vecinal

Sistema backend basado en microservicios desarrollado con **Java Spring Boot** para la gestión de reportes ciudadanos de incidentes delictivos en tiempo real.

---

# 📌 Descripción

**Alerta Vecinal** es una plataforma backend orientada al registro y gestión de incidentes mediante una arquitectura de microservicios.

El sistema permite que los ciudadanos puedan reportar incidentes enviando información como:

- Tipo de incidente
- Descripción
- Ubicación GPS
- Evidencia fotográfica

Los reportes son procesados por distintos microservicios, permitiendo a las autoridades gestionar la atención y seguimiento de cada caso.

El proyecto está enfocado en el desarrollo de APIs REST, autenticación segura y comunicación entre microservicios utilizando Spring Boot y MySQL.

---

# 🎯 Objetivo General

Desarrollar un sistema backend basado en microservicios con Spring Boot que permita registrar y gestionar incidentes delictivos en tiempo real.

---

# ✅ Objetivos Específicos

- Implementar autenticación mediante JWT.
- Gestionar usuarios y roles.
- Registrar incidentes mediante APIs REST.
- Gestionar estados de atención.
- Aplicar arquitectura basada en microservicios.
- Persistir información utilizando MySQL.

---

# 🏗️ Arquitectura del Sistema

El sistema estará dividido en microservicios independientes.

## 🔐 Auth Service

Microservicio encargado de:

- Registro de usuarios
- Inicio de sesión
- Generación de JWT
- Gestión de roles
- Encriptación de contraseñas con BCrypt

---

## 🚨 Incident Service

Microservicio encargado de:

- Registrar incidentes
- Actualizar estados
- Consultar reportes
- Historial de incidencias

---

## 👮 Authority Service

Microservicio encargado de:

- Gestión de policías
- Gestión de ronderos
- Consulta de autoridades

---

## 📍 Location Service

Microservicio encargado de:

- Procesamiento de coordenadas GPS
- Gestión de ubicaciones
- Consulta de distritos

---

# 👥 Roles del Sistema

## 👤 Ciudadano

Funciones:

- Registrar incidentes
- Consultar reportes
- Ver estado de atención

---

## 👮 Policía

Funciones:

- Consultar incidentes
- Actualizar estados
- Gestionar atención

### Estados disponibles

- Pendiente
- En proceso
- Atendido

---

## ⚙️ Administrador

Funciones:

- Gestionar usuarios
- Gestionar categorías
- Gestionar incidentes
- Visualizar estadísticas

---

# 🔄 Flujo del Sistema

## 1️⃣ Registro del incidente

El ciudadano envía:

- Tipo de incidente
- Descripción
- Coordenadas GPS
- Evidencia fotográfica

---

## 2️⃣ Procesamiento

El microservicio de incidentes procesa y almacena la información en MySQL.

---

## 3️⃣ Gestión del incidente

La autoridad consulta los incidentes y actualiza el estado del caso.

---

## 4️⃣ Seguimiento

El ciudadano consulta el estado del incidente mediante la API REST.

---

# 🛠️ Tecnologías Utilizadas

## Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA

## Base de Datos

- MySQL

## Arquitectura

- Microservicios
- API REST
- JWT Authentication

## Seguridad

- BCrypt Password Encoder
- JWT Authentication
- Roles y permisos

## Herramientas

- Maven
- Docker
- Postman
- GitHub

---

# 🗄️ Entidades Principales

- Usuarios
- Roles
- Incidentes
- Categorías
- Evidencias
- Estados
- Autoridades

---

# 📡 Endpoints Principales

## 🔐 Auth Service

```http
POST /api/auth/login
POST /api/auth/register
```

---

## 🚨 Incident Service

```http
POST /api/incidents
GET /api/incidents
GET /api/incidents/{id}
PUT /api/incidents/{id}
```

---

## 👮 Authority Service

```http
GET /api/police
GET /api/ronderos
```

---

# 🔐 Seguridad

El sistema implementa mecanismos de seguridad utilizando:

- JWT Authentication
- BCrypt para encriptación de contraseñas
- Control de acceso por roles
- Middleware de autorización
- Validación de endpoints

### Ejemplo BCrypt

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

# 📂 Estructura General

```bash
alerta-vecinal/
│
├── auth-service/
├── incident-service/
├── authority-service/
├── location-service/
│
├── docker-compose.yml
└── README.md
```

---

# 📊 Beneficios

- Arquitectura escalable
- Servicios independientes
- Fácil mantenimiento
- APIs reutilizables
- Mejor organización del backend

---

# 🚀 Mejoras Futuras

- API Gateway
- Eureka Server
- RabbitMQ
- Docker Compose
- Kubernetes
- Notificaciones en tiempo real

---

# 📦 Instalación

## Clonar repositorio

```bash
git clone https://github.com/usuario/alerta-vecinal.git
```

## Ingresar al proyecto

```bash
cd alerta-vecinal
```

## Ejecutar microservicios

```bash
mvn spring-boot:run
```

---

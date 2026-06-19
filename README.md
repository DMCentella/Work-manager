Descripción General

El Sistema de Control de Empleados es una aplicación empresarial desarrollada bajo una arquitectura de microservicios, cuyo objetivo es gestionar empleados, tareas, asistencias, usuarios y notificaciones en tiempo real.

La solución utiliza Spring Boot, Spring Cloud, Angular 18, RabbitMQ y WebSockets, permitiendo una comunicación eficiente tanto entre microservicios como con los usuarios finales.

<img width="325" height="434" alt="image" src="https://github.com/user-attachments/assets/b4272294-50f5-4283-b7e6-4d4fdf366235" />



La aplicación utiliza:

API Gateway como punto único de acceso.
Eureka Server para descubrimiento de servicios.
RabbitMQ para comunicación asíncrona basada en eventos.
WebSockets para notificaciones en tiempo real.
JWT para autenticación y autorización.
Tecnologías Utilizadas
Frontend
Angular 18 (Standalone Components)
TypeScript
Bootstrap 5.3
Font Awesome 6.4
STOMP
SockJS
Backend
Java 17+
Spring Boot
Spring Security
Spring Cloud Gateway
Spring Data JPA
Eureka Server
RabbitMQ
WebSocket/STOMP
MySQL
Microservicios
Auth Service

Responsable de:

Registro de usuarios.
Inicio de sesión.
Generación de JWT.
Validación de tokens.
Gestión de roles.
Roles
Rol	Función
ROLE_ADMIN	Administrador del sistema
ROLE_USER	Empleado
Empleado Service

Gestiona toda la información del personal.

Funcionalidades
Crear empleados.
Consultar empleados.
Actualizar empleados.
Eliminar empleados.
Buscar empleados.
Generar estadísticas.
Exportar PDF.
Información gestionada
Nombre
Apellido
Email
Teléfono
Sexo
Salario
Cargo
Departamento
Fecha de ingreso
Tarea Service

Permite asignar y controlar las tareas de los empleados.

Funcionalidades
Crear tareas.
Consultar tareas por empleado.
Consultar tareas pendientes.
Completar tareas.
Eliminar tareas.
Información gestionada
Descripción.
Fecha de asignación.
Fecha de completado.
Estado.
Zona de entrega.
Placa de vehículo.
Número de mesa.
Meta de cajas.
Total de paquetes.
Asistencia Service

Gestiona el control de asistencia.

Funcionalidades
Registrar asistencia.
Consultar asistencias por empleado.
Contabilizar asistencias diarias.
Exportar reportes PDF.
Eliminar registros.
Estados
PRESENTE
TARDANZA
FALTA
Seguridad

La autenticación se realiza mediante JWT.

Características
Token válido por 8 horas.
Interceptor Angular que agrega automáticamente:
Authorization: Bearer <token>
Protección de rutas mediante:
authGuard
adminGuard
Permisos
Administrador

Puede:

Gestionar empleados.
Gestionar tareas.
Gestionar asistencias.
Exportar reportes.
Acceder al dashboard.
Usuario

Puede:

Ver su panel personal.
Consultar sus tareas.
Completar tareas.
Consultar sus asistencias.
Comunicación Asíncrona con RabbitMQ

El sistema utiliza RabbitMQ para desacoplar la comunicación entre microservicios mediante eventos.

Exchange Principal
notificaciones.exchange

Tipo:

Topic Exchange
Eventos Implementados
Tarea Creada
Routing Key: tarea.creada
Queue: tarea.creada.queue

Se genera cuando se asigna una nueva tarea.

Tarea Completada
Routing Key: tarea.completada
Queue: tarea.completada.queue

Se genera cuando un empleado completa una tarea.

Asistencia Registrada
Routing Key: asistencia.registrada
Queue: asistencia.registrada.queue

Se genera al registrar una asistencia.

Empleado Cesado
Routing Key: empleado.cesado
Queue: empleado.cesado.queue

Se genera cuando un empleado es dado de baja.

Notificaciones en Tiempo Real

Las notificaciones se envían mediante WebSockets utilizando STOMP y SockJS.

Canales
Notificaciones por empleado
/topic/notificaciones/{empleadoId}

Cada empleado recibe únicamente sus propias notificaciones.

Dashboard administrativo
/topic/dashboard

Permite actualizar información global para administradores.

Frontend Angular

Características principales:

Angular 18 Standalone Components.
Lazy Loading mediante loadComponent().
JWT almacenado en LocalStorage.
Interceptor HTTP global.
Guards de seguridad.
Componentes separados en:
HTML
CSS
TypeScript

No utiliza Angular Material ni NgModules.

Bases de Datos

Cada microservicio posee su propia base de datos independiente.

Servicio	Base de Datos
Auth Service	auth_db
Empleado Service	empleados_db
Asistencia Service	asistencias_db
Tarea Service	tareas_db

Esto garantiza independencia y escalabilidad entre servicios.

Funcionalidades Implementadas

✅ Arquitectura de Microservicios
✅ API Gateway
✅ Eureka Server
✅ Spring Security + JWT
✅ Angular 18 Standalone
✅ CRUD de Empleados
✅ Gestión de Tareas
✅ Control de Asistencia
✅ Dashboard Administrativo
✅ Exportación de Reportes PDF
✅ RabbitMQ (Mensajería Asíncrona)
✅ WebSockets (Notificaciones en Tiempo Real)
✅ Roles y Permisos
✅ Comunicación REST entre Cliente y Gateway

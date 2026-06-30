# VetNova - Plataforma Veterinaria basada en Microservicios

## Integrantes

- Pamela Acuña
- Gabriel Martínez
- Daniel Castro

## Descripción

VetNova es una solución distribuida para la gestión integral de una clínica veterinaria, desarrollada bajo arquitectura de microservicios con Spring Boot.

La plataforma permite administrar clientes, mascotas, agenda médica, atención clínica, ventas, inventario, notificaciones, sucursales, autenticación y usuarios.

---

# Microservicios Implementados

| Microservicio | Puerto |
|--------------|---------|
| Auth | 8081 |
| Usuarios | 8082 |
| Atención Clínica | 8083 |
| Clientes | 8084 |
| Mascotas | 8085 |
| Agenda | 8086 |
| Inventario | 8087 |
| Ventas | 8088 |
| Notificaciones | 8089 |
| Sucursales | 8090 |

---

# Integraciones

## Agenda → Notificaciones

Al registrar una cita se envía una notificación automática.

## Inventario → Notificaciones

Cuando el stock llega al mínimo se genera una alerta.

## Atención Clínica → Notificaciones

Al emitir recetas o certificados se generan eventos y notificaciones.

---

# API Gateway

Puerto:

8099

Rutas principales:

- /api/v1/auth/**
- /api/v1/usuarios/**
- /api/v1/clientes/**
- /api/v1/mascotas/**
- /api/v1/agenda/**
- /api/v1/atenciones/**
- /api/v1/inventario/**
- /api/v1/ventas/**
- /api/v1/notificaciones/**
- /api/v1/sucursales/**

---

# Swagger

Cada microservicio expone documentación OpenAPI mediante Swagger UI.

Ejemplos:

http://localhost:8081/swagger-ui.html

http://localhost:8082/swagger-ui.html

http://localhost:8083/swagger-ui.html

...

---

# Tecnologías

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Validation
- Spring Cloud Gateway
- MySQL
- Swagger OpenAPI
- JUnit 5
- Mockito
- Maven

---

# Ejecución

1. Levantar las bases de datos.
2. Ejecutar los microservicios.
3. Ejecutar API Gateway.
4. Acceder a Swagger.
5. Probar mediante Postman o Gateway.

---

# Cobertura de pruebas

Se implementaron pruebas unitarias con JUnit y Mockito utilizando JaCoCo para medir cobertura.
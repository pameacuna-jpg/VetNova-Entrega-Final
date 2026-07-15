# VetNova - Microservicios Pamela Acuña

## Descripción

Este repositorio contiene tres microservicios desarrollados para el sistema veterinario VetNova utilizando Spring Boot, MySQL, JPA, Maven y arquitectura basada en microservicios.

Los microservicios implementan lógica de negocio independiente, persistencia propia, validaciones, pruebas unitarias y comunicación mediante servicios REST.

---

## Microservicios Implementados

| Microservicio                | Puerto | Función                                                 |
| ---------------------------- | ------ | ------------------------------------------------------- |
| ms-inventario                | 8087   | Gestión de productos, stock y movimientos de inventario |
| ms-notificaciones            | 8089   | Registro y gestión de alertas y notificaciones          |
| ms-sucursales-administracion | 8090   | Administración de sucursales, boxes y horarios          |

---

## Tecnologías Utilizadas

* Java 21
* Spring Boot
* Spring Data JPA
* Maven
* MySQL
* Lombok
* Bean Validation
* JUnit 5
* Mockito
* Postman
* GitHub

---

## Arquitectura

Cada microservicio implementa una arquitectura en capas:

Controller
↓
Service
↓
Repository
↓
MySQL

Cada servicio posee su propia base de datos y expone endpoints REST independientes.

---

## Comunicación entre Microservicios

Actualmente se encuentra implementada la comunicación:

Inventario → Notificaciones

Flujo:

Movimiento de Inventario
↓
Verificación de stock
↓
Detección de stock bajo
↓
Generación automática de notificación

Esta comunicación se realiza mediante llamadas REST utilizando RestTemplate.

---

## Persistencia

Motor de Base de Datos:

MySQL

Cada microservicio mantiene independencia de datos y persistencia propia.

---

## Pruebas Unitarias

Se implementaron pruebas unitarias utilizando:

* JUnit 5
* Mockito

Resultados:

| Microservicio  | Pruebas |
| -------------- | ------- |
| Inventario     | 7       |
| Notificaciones | 8       |
| Sucursales     | 7       |

Total:

22 pruebas unitarias exitosas

Resultado:

BUILD SUCCESS

---

## Endpoints Principales

### Inventario

GET /api/v1/productos

POST /api/v1/productos

POST /api/v1/movimientos

GET /api/v1/productos/bajo-stock

---

### Notificaciones

GET /api/v1/notificaciones

POST /api/v1/notificaciones

GET /api/v1/notificaciones/{id}

---

### Proveedores

GET /api/v1/proveedores

GET /api/v1/proveedores/{id}

POST /api/v1/proveedores

PUT /api/v1/proveedores/{id}

DELETE /api/v1/proveedores/{id}

GET /api/v1/proveedores/buscar/{nombre}

---

### Sucursales

GET /api/v1/sucursales

POST /api/v1/sucursales

GET /api/v1/sucursales/{id}

---

## Integración con VetNova

Estos microservicios forman parte de la solución distribuida VetNova junto a los módulos:

* Clientes
* Mascotas
* Usuarios y Roles
* Agenda
* Atención Clínica
* Ventas y Pagos
* Autenticación y Seguridad

La arquitectura permite la integración mediante APIs REST y DTOs compartidos entre dominios del negocio.
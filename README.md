# 🐾 VetNova - Plataforma de Gestión Veterinaria basada en Microservicios

## Integrantes

- Pamela Acuña
- Gabriel Martínez
- Daniel Castro

Carrera: Ingeniería en Informática  
Asignatura: Desarrollo Fullstack I  
Institución: DUOC UC

---

# Descripción del Proyecto

VetNova es una plataforma de gestión veterinaria desarrollada bajo una arquitectura de microservicios utilizando Spring Boot.

El objetivo es administrar los principales procesos de una clínica veterinaria:

- Gestión de usuarios
- Gestión de clientes
- Gestión de mascotas
- Agenda de horas
- Atención clínica
- Inventario
- Ventas
- Notificaciones
- Seguridad y autenticación
- Administración de sucursales

---

# Arquitectura

El sistema fue diseñado siguiendo el patrón:

Controller → Service → Repository → Base de Datos

y aplicando principios de:

- Arquitectura de Microservicios
- REST API
- DTO Pattern
- Validaciones Bean Validation
- Manejo Global de Excepciones
- API Gateway
- Swagger OpenAPI
- Pruebas Unitarias con JUnit y Mockito

---

# Microservicios Implementados

| Microservicio | Puerto |
|---------------|---------|
| Auth | 8081 |
| Usuarios | 8082 |
| Atención Clínica | 8083 |
| Clientes | 8084 |
| Mascotas | 8085 |
| Agenda | 8086 |
| Inventario | 8087 |
| Notificaciones | 8089 |
| Sucursales | 8090 |
| Ventas | 8091 |
| API Gateway | 8099 |

---

# Integraciones Implementadas

## Inventario → Notificaciones

Cuando el stock alcanza o baja del stock mínimo:

- Se genera un evento de stock bajo.
- Se crea una notificación automática.

---

## Agenda → Notificaciones

Al registrar una atención:

- Se genera una notificación para el cliente.

---

## Atención Clínica → Notificaciones

Al emitir:

- Recetas
- Certificados

se genera una notificación automática.

---

## Clientes ↔ Mascotas

Las mascotas se encuentran asociadas a un propietario mediante el identificador del cliente.

---

# Tecnologías Utilizadas

## Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Validation
- Spring Security
- Spring Cloud Gateway

## Persistencia

- MySQL

## Documentación

- Swagger OpenAPI

## Testing

- JUnit 5
- Mockito
- MockMvc
- JaCoCo

---

# API Gateway

Puerto:

```text
http://localhost:8099
```

Permite centralizar el acceso a todos los microservicios.

---

# Swagger

Una vez iniciados los microservicios:

## Auth

```text
http://localhost:8081/swagger-ui.html
```

## Usuarios

```text
http://localhost:8082/swagger-ui.html
```

## Atención Clínica

```text
http://localhost:8083/swagger-ui.html
```

## Clientes

```text
http://localhost:8084/swagger-ui.html
```

## Mascotas

```text
http://localhost:8085/swagger-ui.html
```

## Agenda

```text
http://localhost:8086/swagger-ui.html
```

## Inventario

```text
http://localhost:8087/swagger-ui.html
```

## Notificaciones

```text
http://localhost:8089/swagger-ui.html
```

## Sucursales

```text
http://localhost:8090/swagger-ui.html
```

## Ventas

```text
http://localhost:8091/swagger-ui.html
```

---

# Cobertura de Pruebas

Cobertura obtenida mediante JaCoCo:

| Microservicio | Cobertura |
|---------------|------------|
| Usuarios | 95% |
| Clientes | 91% |
| Inventario | ~90% |
| Mascotas | 88% |
| Notificaciones | 81% |

Las pruebas validan:

- Reglas de negocio
- Validaciones
- Endpoints REST
- Manejo de errores
- Integración entre capas

---

# Ejecución del Proyecto

## Compilar

```bash
mvn clean install
```

## Ejecutar

```bash
mvn spring-boot:run
```

o

```bash
.\mvnw.cmd spring-boot:run
```

---

# Estructura General

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
MySQL
```

---

# Conclusiones

VetNova implementa una arquitectura moderna basada en microservicios, permitiendo escalabilidad, desacoplamiento y mantenibilidad.

La solución incorpora:

- API Gateway
- Swagger/OpenAPI
- Persistencia MySQL
- Comunicación REST
- Pruebas Unitarias
- Cobertura JaCoCo
- Buenas prácticas de desarrollo Spring Boot
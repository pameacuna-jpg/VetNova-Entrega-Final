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

Rutas principales (coinciden 1 a 1 con `api-gateway/src/main/resources/application.yml`):

- /api/v1/auth/** → auth-service (8081)
- /api/v1/usuarios/** → usuarios-service (8082)
- /api/v1/atenciones/** → atencion-clinica-service (8083)
- /api/v1/clientes/** → clientes-service (8084)
- /api/v1/mascotas/** → mascotas-service (8085)
- /api/v1/citas/** → agenda-service (8086)
- /api/v1/productos/** → inventario-productos-service (8087)
- /api/v1/movimientos/** → inventario-movimientos-service (8087)
- /api/v1/proveedores/** → inventario-proveedores-service (8087)
- /api/v1/ventas/** → ventas-service (8088)
- /api/v1/notificaciones/** → notificaciones-service (8089)
- /api/v1/sucursales/** → sucursales-service (8090)
- /api/v1/boxes/** → boxes-service (8090)
- /api/v1/horarios/** → horarios-service (8090)

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
Los reportes se generan por microservicio en `target/site/jacoco/index.html` (ejecutar `mvn test` o
usar `Abrir_Reportes_JaCoCo.ps1` en la raíz del repositorio).

---

# Gestión del proyecto

- **Tablero de trabajo (Trello):** [ENLACE TRELLO] — backlog, roles y seguimiento de historias de usuario por integrante.
- **Repositorio GitHub:** [ENLACE REPOSITORIO GITHUB]
- Cada microservicio conserva su propio README con el detalle de endpoints, entidades y pruebas.

---

# Despliegue

## Local (obligatorio para la defensa)

1. Tener MySQL corriendo en `localhost:3306` (usuario `root`, sin password) — puede ser local o vía Docker:
   ```
   docker run --name vetnova-mysql -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 3306:3306 -d mysql:8
   ```
2. Levantar cada microservicio de forma independiente (IDE o `./mvnw spring-boot:run` dentro de cada carpeta).
3. Levantar `api-gateway` al final, una vez que los microservicios estén arriba (puerto 8099).
4. Verificar Swagger de cada servicio en `http://localhost:<puerto>/swagger-ui.html`.
5. Probar el flujo completo a través del Gateway (`http://localhost:8099/api/v1/...`) o directo contra cada servicio.

## Remoto

[DETALLE DESPLIEGUE REMOTO: Railway / Render — completar si el equipo despliega en un entorno remoto.
 Indicar aquí las URLs públicas de cada microservicio y del Gateway una vez desplegados.]
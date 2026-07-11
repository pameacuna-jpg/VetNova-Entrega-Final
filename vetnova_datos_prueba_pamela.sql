-- =============================================================
-- VetNova - Datos de prueba para MS Pamela
-- Microservicios cubiertos:
--   1) ms-sucursales-administracion (vetnova_sucursales)
--   2) ms-inventario              (vetnova_inventario)
--   3) ms-notificaciones          (vetnova_notificaciones)
--
-- Uso recomendado:
--   1. Detener los tres microservicios.
--   2. Ejecutar este archivo completo en phpMyAdmin / MySQL.
--   3. Levantar Sucursales (8090), Notificaciones (8089) e Inventario (8087).
--
-- El script es repetible: elimina solo los datos de prueba de estas tablas
-- y vuelve a insertarlos con IDs conocidos para facilitar Swagger.
-- =============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================
-- 1. BASE DE DATOS: SUCURSALES
-- =============================================================
CREATE DATABASE IF NOT EXISTS vetnova_sucursales
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE vetnova_sucursales;

CREATE TABLE IF NOT EXISTS sucursales (
    id_sucursal BIGINT NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_sucursal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS boxes_atencion (
    id_box BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo_atencion VARCHAR(100) NOT NULL,
    id_sucursal BIGINT NOT NULL,
    disponible BIT NOT NULL,
    PRIMARY KEY (id_box)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS horarios_sucursal (
    id_horario BIGINT NOT NULL,
    dia VARCHAR(30) NOT NULL,
    hora_apertura VARCHAR(20) NOT NULL,
    hora_cierre VARCHAR(20) NOT NULL,
    id_sucursal BIGINT NOT NULL,
    activo BIT NOT NULL,
    PRIMARY KEY (id_horario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tablas de secuencia utilizadas por GenerationType.SEQUENCE en MySQL/Hibernate.
CREATE TABLE IF NOT EXISTS sucursal_seq (next_val BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS box_seq (next_val BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS horario_seq (next_val BIGINT NOT NULL);

DELETE FROM horarios_sucursal;
DELETE FROM boxes_atencion;
DELETE FROM sucursales;
DELETE FROM sucursal_seq;
DELETE FROM box_seq;
DELETE FROM horario_seq;

INSERT INTO sucursal_seq (next_val) VALUES (100);
INSERT INTO box_seq (next_val) VALUES (100);
INSERT INTO horario_seq (next_val) VALUES (100);

INSERT INTO sucursales
    (id_sucursal, nombre, direccion, telefono, ciudad, estado)
VALUES
    (1, 'VetNova Concepción Centro', 'Av. Los Carrera 1234', '+56 41 222 1001', 'Concepción', 'ACTIVA'),
    (2, 'VetNova San Pedro', 'Av. Michaihue 850', '+56 41 222 1002', 'San Pedro de la Paz', 'ACTIVA'),
    (3, 'VetNova Talcahuano', 'Av. Colón 2450', '+56 41 222 1003', 'Talcahuano', 'INACTIVA');

INSERT INTO boxes_atencion
    (id_box, nombre, tipo_atencion, id_sucursal, disponible)
VALUES
    (1, 'Box General 1', 'CONSULTA_GENERAL', 1, b'1'),
    (2, 'Box Procedimientos', 'PROCEDIMIENTOS', 1, b'1'),
    (3, 'Box General 2', 'CONSULTA_GENERAL', 2, b'1'),
    (4, 'Box Vacunación', 'VACUNACION', 2, b'0');

INSERT INTO horarios_sucursal
    (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo)
VALUES
    (1, 'LUNES',    '08:30', '19:00', 1, b'1'),
    (2, 'MARTES',   '08:30', '19:00', 1, b'1'),
    (3, 'MIERCOLES','08:30', '19:00', 1, b'1'),
    (4, 'JUEVES',   '08:30', '19:00', 1, b'1'),
    (5, 'VIERNES',  '08:30', '19:00', 1, b'1'),
    (6, 'LUNES',    '09:00', '18:00', 2, b'1'),
    (7, 'MARTES',   '09:00', '18:00', 2, b'1'),
    (8, 'MIERCOLES','09:00', '18:00', 2, b'1'),
    (9, 'JUEVES',   '09:00', '18:00', 2, b'1'),
    (10,'VIERNES',  '09:00', '18:00', 2, b'1');

-- =============================================================
-- 2. BASE DE DATOS: INVENTARIO
-- =============================================================
CREATE DATABASE IF NOT EXISTS vetnova_inventario
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE vetnova_inventario;

CREATE TABLE IF NOT EXISTS productos (
    id_producto BIGINT NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    precio INT NOT NULL,
    stock_actual INT NOT NULL,
    stock_minimo INT NOT NULL,
    activo BIT NOT NULL,
    PRIMARY KEY (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS proveedores (
    id_proveedor BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    activo BIT NOT NULL,
    PRIMARY KEY (id_proveedor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id_movimiento BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    id_sucursal BIGINT NOT NULL,
    tipo_movimiento VARCHAR(30) NOT NULL,
    cantidad INT NOT NULL,
    observacion VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_movimiento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS producto_seq (next_val BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS proveedor_seq (next_val BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS movimiento_seq (next_val BIGINT NOT NULL);

DELETE FROM movimientos_inventario;
DELETE FROM productos;
DELETE FROM proveedores;
DELETE FROM producto_seq;
DELETE FROM proveedor_seq;
DELETE FROM movimiento_seq;

INSERT INTO producto_seq (next_val) VALUES (100);
INSERT INTO proveedor_seq (next_val) VALUES (100);
INSERT INTO movimiento_seq (next_val) VALUES (100);

INSERT INTO proveedores
    (id_proveedor, nombre, telefono, email, direccion, activo)
VALUES
    (1, 'Laboratorio Veterinario BioVet', '+56 2 2450 1000', 'ventas@biovet.cl', 'Av. Industrial 450, Santiago', b'1'),
    (2, 'Distribuidora Mascota Salud', '+56 41 245 2000', 'pedidos@mascotaysalud.cl', 'Paicaví 2200, Concepción', b'1'),
    (3, 'Insumos Clínicos del Sur', '+56 41 260 3000', 'contacto@insumosdelsur.cl', 'Ruta 160 Km 12, Coronel', b'1');

INSERT INTO productos
    (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo)
VALUES
    -- Producto 1: ideal para demostrar stock bajo. Salida de 6 deja stock 4 <= mínimo 5.
    (1, 'Vacuna Antirrábica', 'VACUNAS', 15000, 10, 5, b'1'),
    (2, 'Antiparasitario Oral Canino', 'MEDICAMENTOS', 8990, 25, 8, b'1'),
    (3, 'Jeringa Desechable 5 ml', 'INSUMOS', 450, 100, 30, b'1'),
    (4, 'Alimento Renal 2 kg', 'ALIMENTOS', 24990, 12, 4, b'1'),
    (5, 'Collar Isabelino Mediano', 'ACCESORIOS', 7990, 8, 3, b'1'),
    (6, 'Producto Inactivo de Prueba', 'OTROS', 1000, 5, 2, b'0');

-- Movimientos históricos iniciales. No dejan productos bajo mínimo.
INSERT INTO movimientos_inventario
    (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion)
VALUES
    (1, 2, 1, 'ENTRADA', 25, 'Carga inicial de antiparasitarios'),
    (2, 3, 1, 'ENTRADA', 100, 'Carga inicial de insumos'),
    (3, 4, 2, 'ENTRADA', 12, 'Carga inicial de alimento renal');

-- =============================================================
-- 3. BASE DE DATOS: NOTIFICACIONES
-- =============================================================
CREATE DATABASE IF NOT EXISTS vetnova_notificaciones
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE vetnova_notificaciones;

CREATE TABLE IF NOT EXISTS notificaciones (
    id_notificacion BIGINT NOT NULL AUTO_INCREMENT,
    destino VARCHAR(30) NOT NULL,
    id_cliente BIGINT NULL,
    destinatario VARCHAR(120) NOT NULL,
    mensaje VARCHAR(500) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    canal VARCHAR(30) NOT NULL,
    prioridad VARCHAR(30) NOT NULL,
    fecha_creacion DATETIME(6) NOT NULL,
    PRIMARY KEY (id_notificacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELETE FROM notificaciones;
ALTER TABLE notificaciones AUTO_INCREMENT = 1;

INSERT INTO notificaciones
    (destino, id_cliente, destinatario, mensaje, tipo, estado, canal, prioridad, fecha_creacion)
VALUES
    ('INTERNA', NULL, 'inventario@vetnova.cl',
     'Notificación inicial de prueba: sistema de inventario operativo.',
     'STOCK_BAJO', 'PENDIENTE', 'EMAIL', 'MEDIA', NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================
-- CONSULTAS DE VERIFICACIÓN
-- =============================================================
SELECT 'SUCURSALES' AS bloque, COUNT(*) AS registros FROM vetnova_sucursales.sucursales;
SELECT 'BOXES' AS bloque, COUNT(*) AS registros FROM vetnova_sucursales.boxes_atencion;
SELECT 'HORARIOS' AS bloque, COUNT(*) AS registros FROM vetnova_sucursales.horarios_sucursal;
SELECT 'PRODUCTOS' AS bloque, COUNT(*) AS registros FROM vetnova_inventario.productos;
SELECT 'PROVEEDORES' AS bloque, COUNT(*) AS registros FROM vetnova_inventario.proveedores;
SELECT 'MOVIMIENTOS' AS bloque, COUNT(*) AS registros FROM vetnova_inventario.movimientos_inventario;
SELECT 'NOTIFICACIONES' AS bloque, COUNT(*) AS registros FROM vetnova_notificaciones.notificaciones;

-- ============================================================================
-- 1. POBLAR CLIENTES (id_cliente se autogenerará como 1, 2 y 3)
-- ============================================================================
INSERT INTO clientes (rut, nombre, telefono, email, direccion, estado, fecha_creacion, fecha_ultima_modificacion) 
VALUES ('19.876.543-2', 'Pamela Acuña', '+56911112222', 'pamela@vetnova.cl', 'Av. Bernardo O''Higgins 123, Concepcion', 'ACTIVO', '2026-01-10 14:30:00', NULL);

INSERT INTO clientes (rut, nombre, telefono, email, direccion, estado, fecha_creacion, fecha_ultima_modificacion) 
VALUES ('12.345.678-9', 'Gabriel Martinez', '+56933334444', 'gabriel@vetnova.cl', 'Calle Los Carrera 456, Chiguayante', 'ACTIVO', '2026-02-15 09:15:00', NULL);

INSERT INTO clientes (rut, nombre, telefono, email, direccion, estado, fecha_creacion, fecha_ultima_modificacion) 
VALUES ('8.123.456-K', 'Juan Perez Viejo', '+56955556666', 'juan@correo.cl', 'Pasaje Las Rosas 789, Talcahuano', 'INACTIVO', '2025-05-20 11:00:00', NULL);


-- ============================================================================
-- 2. POBLAR CONTACTOS DE EMERGENCIA (Asociados a id_cliente = 1 y 2)
-- ============================================================================
-- Contactos para Pamela (id_cliente = 1)
INSERT INTO contactos_emergencia (nombre, telefono, parentesco, id_cliente) 
VALUES ('Carlos Acuña', '+56988887777', 'Hermano', 1);

-- Contactos para Gabriel (id_cliente = 2)
INSERT INTO contactos_emergencia (nombre, telefono, parentesco, id_cliente) 
VALUES ('Maria Martinez', '+56999998888', 'Madre', 2);


-- ============================================================================
-- 3. POBLAR HISTORIALES DE CLIENTE (Asociados a id_cliente = 1, 2 y 3)
-- ============================================================================
INSERT INTO historiales_cliente (total_compras, total_atenciones, fecha_ultima_atencion, id_cliente) 
VALUES (5, 3, '2026-06-15', 1);

INSERT INTO historiales_cliente (total_compras, total_atenciones, fecha_ultima_atencion, id_cliente) 
VALUES (2, 1, '2026-05-20', 2);

INSERT INTO historiales_cliente (total_compras, total_atenciones, fecha_ultima_atencion, id_cliente) 
VALUES (0, 0, NULL, 3);
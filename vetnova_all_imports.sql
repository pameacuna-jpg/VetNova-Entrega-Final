-- ============================================================
-- VetNova — Seed Completo v2 (todas las bases de datos)
-- Ejecutar directamente en MySQL/MariaDB via phpMyAdmin
-- Borra y recarga todos los datos de prueba
-- ============================================================

-- ============================================================
-- 1. MS AUTH — vetnova_auth
-- ============================================================
USE vetnova_auth;

DELETE FROM auth_usuarios;

INSERT INTO auth_usuarios (email, password, rol, activo, fecha_creacion) VALUES ('admin@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'ADMIN', true, NOW());
INSERT INTO auth_usuarios (email, password, rol, activo, fecha_creacion) VALUES ('veterinario@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'VETERINARIO', true, NOW());
INSERT INTO auth_usuarios (email, password, rol, activo, fecha_creacion) VALUES ('recepcion@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'RECEPCIONISTA', true, NOW());
INSERT INTO auth_usuarios (email, password, rol, activo, fecha_creacion) VALUES ('cliente1@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'CLIENTE', true, NOW());
INSERT INTO auth_usuarios (email, password, rol, activo, fecha_creacion) VALUES ('cliente2@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'CLIENTE', true, NOW());

-- ============================================================
-- 2. MS USUARIOS — vetnova_usuarios
-- ============================================================
USE vetnova_usuarios;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM usuarios_roles;
DELETE FROM roles_permisos;
DELETE FROM usuarios;
DELETE FROM roles;
DELETE FROM permisos;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO permisos (nombre_permiso, modulo) VALUES ('CREAR_USUARIO', 'USUARIOS');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('EDITAR_USUARIO', 'USUARIOS');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('DESACTIVAR_USUARIO', 'USUARIOS');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('VER_CLIENTES', 'CLIENTES');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('CREAR_CLIENTE', 'CLIENTES');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('EDITAR_CLIENTE', 'CLIENTES');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('VER_MASCOTAS', 'MASCOTAS');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('CREAR_MASCOTA', 'MASCOTAS');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('AGENDAR_CITA', 'AGENDA');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('CANCELAR_CITA', 'AGENDA');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('VER_CITAS', 'AGENDA');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('REGISTRAR_ATENCION', 'ATENCION');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('VER_INVENTARIO', 'INVENTARIO');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('GESTIONAR_INVENTARIO', 'INVENTARIO');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('PROCESAR_VENTA', 'VENTAS');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('VER_VENTAS', 'VENTAS');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('GESTIONAR_SUCURSAL', 'SUCURSALES');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('VER_NOTIFICACIONES', 'NOTIFICACIONES');

INSERT INTO roles (nombre_rol, descripcion) VALUES ('ADMIN', 'Acceso total al sistema');
INSERT INTO roles (nombre_rol, descripcion) VALUES ('VETERINARIO', 'Atencion clinica, agenda y fichas de mascotas');
INSERT INTO roles (nombre_rol, descripcion) VALUES ('RECEPCIONISTA', 'Gestion de clientes, citas y ventas');
INSERT INTO roles (nombre_rol, descripcion) VALUES ('CLIENTE', 'Consulta de sus propias mascotas y citas');

INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,1);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,2);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,3);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,4);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,5);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,6);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,7);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,8);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,9);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,10);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,11);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,12);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,13);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,14);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,15);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,16);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,17);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1,18);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (2,4);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (2,6);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (2,7);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (2,8);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (2,9);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (2,11);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (2,12);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (2,13);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,4);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,5);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,6);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,7);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,9);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,10);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,11);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,15);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,16);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (3,18);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (4,7);
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (4,11);

INSERT INTO usuarios (nombre, email, password, estado, id_sucursal) VALUES ('Administrador Sistema', 'admin@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'ACTIVO', 1);
INSERT INTO usuarios (nombre, email, password, estado, id_sucursal) VALUES ('Dra. Sofia Ramirez', 'veterinario@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'ACTIVO', 1);
INSERT INTO usuarios (nombre, email, password, estado, id_sucursal) VALUES ('Laura Fuentes', 'recepcion@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'ACTIVO', 1);
INSERT INTO usuarios (nombre, email, password, estado, id_sucursal) VALUES ('Juan Perez', 'cliente1@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'ACTIVO', 1);
INSERT INTO usuarios (nombre, email, password, estado, id_sucursal) VALUES ('Maria Gonzalez', 'cliente2@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'ACTIVO', 1);

INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion) VALUES (1, 1, NOW());
INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion) VALUES (2, 2, NOW());
INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion) VALUES (3, 3, NOW());
INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion) VALUES (4, 4, NOW());
INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion) VALUES (5, 4, NOW());

-- ============================================================
-- 3. MS SUCURSALES — vetnova_sucursales
-- ============================================================
USE vetnova_sucursales;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM horarios_sucursal;
DELETE FROM boxes_atencion;
DELETE FROM sucursales;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO sucursales (id_sucursal, nombre, direccion, telefono, ciudad, estado) VALUES (1, 'VetNova Centro', 'Av. Providencia 1234, Piso 2', '+56222345678', 'Santiago', 'ACTIVA');
INSERT INTO sucursales (id_sucursal, nombre, direccion, telefono, ciudad, estado) VALUES (2, 'VetNova Las Condes', 'Apoquindo 5678, Local 10', '+56222987654', 'Las Condes', 'ACTIVA');
INSERT INTO sucursales (id_sucursal, nombre, direccion, telefono, ciudad, estado) VALUES (3, 'VetNova Maipu', 'Gran Avenida 321', '+56222456789', 'Maipu', 'ACTIVA');

INSERT INTO boxes_atencion (id_box, nombre, tipo_atencion, id_sucursal, disponible) VALUES (1, 'Box 1', 'CONSULTA', 1, true);
INSERT INTO boxes_atencion (id_box, nombre, tipo_atencion, id_sucursal, disponible) VALUES (2, 'Box 2', 'CIRUGIA', 1, true);
INSERT INTO boxes_atencion (id_box, nombre, tipo_atencion, id_sucursal, disponible) VALUES (3, 'Box 3', 'URGENCIA', 1, true);
INSERT INTO boxes_atencion (id_box, nombre, tipo_atencion, id_sucursal, disponible) VALUES (4, 'Box 4', 'CONSULTA', 2, true);
INSERT INTO boxes_atencion (id_box, nombre, tipo_atencion, id_sucursal, disponible) VALUES (5, 'Box 5', 'CIRUGIA', 2, true);
INSERT INTO boxes_atencion (id_box, nombre, tipo_atencion, id_sucursal, disponible) VALUES (6, 'Box 6', 'CONSULTA', 3, true);

INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (1, 'LUNES', '08:00', '20:00', 1, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (2, 'MARTES', '08:00', '20:00', 1, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (3, 'MIERCOLES', '08:00', '20:00', 1, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (4, 'JUEVES', '08:00', '20:00', 1, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (5, 'VIERNES', '08:00', '20:00', 1, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (6, 'SABADO', '09:00', '14:00', 1, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (7, 'LUNES', '09:00', '19:00', 2, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (8, 'MARTES', '09:00', '19:00', 2, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (9, 'MIERCOLES', '09:00', '19:00', 2, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (10, 'JUEVES', '09:00', '19:00', 2, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (11, 'VIERNES', '09:00', '19:00', 2, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (12, 'LUNES', '08:30', '18:00', 3, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (13, 'MARTES', '08:30', '18:00', 3, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (14, 'MIERCOLES', '08:30', '18:00', 3, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (15, 'JUEVES', '08:30', '18:00', 3, true);
INSERT INTO horarios_sucursal (id_horario, dia, hora_apertura, hora_cierre, id_sucursal, activo) VALUES (16, 'VIERNES', '08:30', '18:00', 3, true);

-- ============================================================
-- 4. MS CLIENTES — vetnova_clientes
-- ============================================================
USE vetnova_clientes;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM historiales_cliente;
DELETE FROM contactos_emergencia;
DELETE FROM clientes;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO clientes (rut, nombre, telefono, email, direccion, estado, fecha_creacion) VALUES ('12.345.678-9', 'Juan Perez Soto', '+56912345678', 'cliente1@vetnova.cl', 'Av. Providencia 123, Santiago', 'ACTIVO', NOW());
INSERT INTO clientes (rut, nombre, telefono, email, direccion, estado, fecha_creacion) VALUES ('98.765.432-1', 'Maria Gonzalez Nunez', '+56987654321', 'cliente2@vetnova.cl', 'Los Aromos 456, Las Condes', 'ACTIVO', NOW());
INSERT INTO clientes (rut, nombre, telefono, email, direccion, estado, fecha_creacion) VALUES ('11.222.333-4', 'Carlos Rojas Mendoza', '+56911223344', 'carlos.rojas@gmail.com', 'Calle Larga 789, Nunoa', 'ACTIVO', NOW());
INSERT INTO clientes (rut, nombre, telefono, email, direccion, estado, fecha_creacion) VALUES ('55.666.777-8', 'Ana Morales Vidal', '+56955667788', 'ana.morales@gmail.com', 'Pasaje Verde 10, Maipu', 'ACTIVO', NOW());
INSERT INTO clientes (rut, nombre, telefono, email, direccion, estado, fecha_creacion) VALUES ('44.555.666-7', 'Pedro Castillo Lopez', '+56944556677', 'pedro.castillo@mail.com', 'Gran Avenida 321, San Miguel', 'ACTIVO', NOW());

INSERT INTO contactos_emergencia (nombre, telefono, parentesco, id_cliente) VALUES ('Rosa Soto', '+56922334455', 'Madre', 1);
INSERT INTO contactos_emergencia (nombre, telefono, parentesco, id_cliente) VALUES ('Luis Perez', '+56933445566', 'Hermano', 1);
INSERT INTO contactos_emergencia (nombre, telefono, parentesco, id_cliente) VALUES ('Carmen Nunez', '+56944556677', 'Madre', 2);
INSERT INTO contactos_emergencia (nombre, telefono, parentesco, id_cliente) VALUES ('Roberto Rojas', '+56955667788', 'Esposo', 3);
INSERT INTO contactos_emergencia (nombre, telefono, parentesco, id_cliente) VALUES ('Elena Morales', '+56966778899', 'Hermana', 4);

INSERT INTO historiales_cliente (total_compras, total_atenciones, fecha_ultima_atencion, id_cliente) VALUES (3, 5, '2026-05-15', 1);
INSERT INTO historiales_cliente (total_compras, total_atenciones, fecha_ultima_atencion, id_cliente) VALUES (1, 2, '2026-06-01', 2);
INSERT INTO historiales_cliente (total_compras, total_atenciones, fecha_ultima_atencion, id_cliente) VALUES (0, 1, '2026-04-20', 3);
INSERT INTO historiales_cliente (total_compras, total_atenciones, fecha_ultima_atencion, id_cliente) VALUES (2, 3, '2026-05-28', 4);
INSERT INTO historiales_cliente (total_compras, total_atenciones, id_cliente) VALUES (0, 0, 5);

-- ============================================================
-- 5. MS MASCOTAS — vetnova_mascotas
-- ============================================================
USE vetnova_mascotas;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM historiales_mascota;
DELETE FROM mascotas;
DELETE FROM especies;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO especies (nombre, descripcion) VALUES ('CANINO', 'Perros de todas las razas');
INSERT INTO especies (nombre, descripcion) VALUES ('FELINO', 'Gatos de todas las razas');
INSERT INTO especies (nombre, descripcion) VALUES ('AVE', 'Aves domesticas y exoticas');
INSERT INTO especies (nombre, descripcion) VALUES ('REPTIL', 'Reptiles domesticos');
INSERT INTO especies (nombre, descripcion) VALUES ('ROEDOR', 'Hamsteres, conejos y similares');

INSERT INTO mascotas (nombre, raza, sexo, fecha_nacimiento, id_cliente, estado, numero_microchip, id_especie) VALUES ('Max', 'Labrador Retriever', 'MACHO', '2020-03-15', 1, 'ACTIVO', 'CHIP-001-MAX', 1);
INSERT INTO mascotas (nombre, raza, sexo, fecha_nacimiento, id_cliente, estado, numero_microchip, id_especie) VALUES ('Luna', 'Siames', 'HEMBRA', '2021-07-22', 1, 'ACTIVO', 'CHIP-002-LUN', 2);
INSERT INTO mascotas (nombre, raza, sexo, fecha_nacimiento, id_cliente, estado, numero_microchip, id_especie) VALUES ('Rocky', 'Bulldog Frances', 'MACHO', '2019-11-01', 2, 'ACTIVO', 'CHIP-003-ROC', 1);
INSERT INTO mascotas (nombre, raza, sexo, fecha_nacimiento, id_cliente, estado, numero_microchip, id_especie) VALUES ('Mia', 'Persa', 'HEMBRA', '2022-01-10', 3, 'ACTIVO', 'CHIP-004-MIA', 2);
INSERT INTO mascotas (nombre, raza, sexo, fecha_nacimiento, id_cliente, estado, numero_microchip, id_especie) VALUES ('Thor', 'Pastor Aleman', 'MACHO', '2018-06-30', 4, 'ACTIVO', 'CHIP-005-THO', 1);
INSERT INTO mascotas (nombre, raza, sexo, fecha_nacimiento, id_cliente, estado, numero_microchip, id_especie) VALUES ('Cleo', 'Ragdoll', 'HEMBRA', '2023-02-14', 5, 'ACTIVO', 'CHIP-006-CLE', 2);

INSERT INTO historiales_mascota (numero_historia_clinica, resumen_clinico, fecha_ultima_atencion, ultimo_peso, esta_esterilizado, alergias_criticas, id_mascota) VALUES ('HC-2024-0001', 'Paciente sano. Vacunas al dia.', '2026-05-10', 30.5, true, NULL, 1);
INSERT INTO historiales_mascota (numero_historia_clinica, resumen_clinico, fecha_ultima_atencion, ultimo_peso, esta_esterilizado, alergias_criticas, id_mascota) VALUES ('HC-2024-0002', 'Control rutinario. Sin observaciones.', '2026-04-22', 4.2, false, NULL, 2);
INSERT INTO historiales_mascota (numero_historia_clinica, resumen_clinico, fecha_ultima_atencion, ultimo_peso, esta_esterilizado, alergias_criticas, id_mascota) VALUES ('HC-2024-0003', 'Dermatitis leve tratada con exito.', '2026-06-01', 12.8, true, 'Polen, mariscos', 3);
INSERT INTO historiales_mascota (numero_historia_clinica, resumen_clinico, fecha_ultima_atencion, ultimo_peso, esta_esterilizado, alergias_criticas, id_mascota) VALUES ('HC-2024-0004', 'Primera consulta. Sana.', '2026-04-20', 3.1, false, NULL, 4);
INSERT INTO historiales_mascota (numero_historia_clinica, resumen_clinico, fecha_ultima_atencion, ultimo_peso, esta_esterilizado, alergias_criticas, id_mascota) VALUES ('HC-2024-0005', 'Control anual. Peso dentro del rango.', '2026-05-28', 34.0, true, NULL, 5);
INSERT INTO historiales_mascota (numero_historia_clinica, resumen_clinico, fecha_ultima_atencion, ultimo_peso, esta_esterilizado, alergias_criticas, id_mascota) VALUES ('HC-2024-0006', 'Cachorra. Primera vacuna aplicada.', '2026-06-10', 2.4, false, NULL, 6);

-- ============================================================
-- 6. MS INVENTARIO — vetnova_inventario
-- ============================================================
USE vetnova_inventario;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM movimientos_inventario;
DELETE FROM productos;
DELETE FROM proveedores;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO proveedores (id_proveedor, nombre, telefono, email, direccion, activo) VALUES (1, 'Distribuidora VetMed', '+56223456789', 'ventas@vetmed.cl', 'Av. Industrial 100, Pudahuel', true);
INSERT INTO proveedores (id_proveedor, nombre, telefono, email, direccion, activo) VALUES (2, 'FarmaAnimal Chile', '+56229876543', 'contacto@farmaanimal.cl', 'Los Leones 200, Providencia', true);
INSERT INTO proveedores (id_proveedor, nombre, telefono, email, direccion, activo) VALUES (3, 'NutriPet Distribuciones', '+56221234567', 'pedidos@nutripet.cl', 'San Pablo 350, Quinta Normal', true);

INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (1, 'Vacuna Antirrabica', 'MEDICAMENTOS', 15000, 50, 10, true);
INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (2, 'Antiparasitario Interno', 'MEDICAMENTOS', 8500, 80, 15, true);
INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (3, 'Antiparasitario Externo', 'MEDICAMENTOS', 12000, 60, 10, true);
INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (4, 'Alimento Premium Canino 5kg', 'ALIMENTOS', 28000, 30, 5, true);
INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (5, 'Alimento Premium Felino 2kg', 'ALIMENTOS', 18000, 40, 8, true);
INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (6, 'Collar Antipulgas', 'ACCESORIOS', 9500, 25, 5, true);
INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (7, 'Jeringa 5ml caja 100u', 'INSUMOS', 7000, 20, 5, true);
INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (8, 'Guantes de Latex caja 50u', 'INSUMOS', 5500, 15, 5, true);
INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (9, 'Shampoo Dermatologico', 'HIGIENE', 11000, 35, 8, true);
INSERT INTO productos (id_producto, nombre, categoria, precio, stock_actual, stock_minimo, activo) VALUES (10, 'Vitaminas Multivitaminico', 'MEDICAMENTOS', 14000, 45, 10, true);

INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (1, 1, 1, 'ENTRADA', 50, 'Stock inicial de apertura');
INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (2, 2, 1, 'ENTRADA', 80, 'Stock inicial de apertura');
INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (3, 3, 1, 'ENTRADA', 60, 'Stock inicial de apertura');
INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (4, 4, 1, 'ENTRADA', 30, 'Stock inicial de apertura');
INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (5, 5, 1, 'ENTRADA', 40, 'Stock inicial de apertura');
INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (6, 6, 1, 'ENTRADA', 25, 'Stock inicial de apertura');
INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (7, 7, 1, 'ENTRADA', 20, 'Stock inicial de apertura');
INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (8, 8, 1, 'ENTRADA', 15, 'Stock inicial de apertura');
INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (9, 9, 1, 'ENTRADA', 35, 'Stock inicial de apertura');
INSERT INTO movimientos_inventario (id_movimiento, id_producto, id_sucursal, tipo_movimiento, cantidad, observacion) VALUES (10, 10, 1, 'ENTRADA', 45, 'Stock inicial de apertura');

-- ============================================================
-- 7. MS AGENDA — vetnova_agenda
-- ============================================================
USE vetnova_agenda;

DELETE FROM citas;

INSERT INTO citas (id_cliente, id_mascota, id_veterinario, fecha_hora, motivo, estado) VALUES (1, 1, 2, '2026-07-05 09:00:00', 'Control anual y vacunacion', 'CONFIRMADA');
INSERT INTO citas (id_cliente, id_mascota, id_veterinario, fecha_hora, motivo, estado) VALUES (1, 2, 2, '2026-07-06 10:30:00', 'Revision post-castracion', 'AGENDADA');
INSERT INTO citas (id_cliente, id_mascota, id_veterinario, fecha_hora, motivo, estado) VALUES (2, 3, 2, '2026-07-07 11:00:00', 'Revision dermatitis recurrente', 'AGENDADA');
INSERT INTO citas (id_cliente, id_mascota, id_veterinario, fecha_hora, motivo, estado) VALUES (3, 4, 2, '2026-07-08 15:00:00', 'Primera consulta general', 'AGENDADA');
INSERT INTO citas (id_cliente, id_mascota, id_veterinario, fecha_hora, motivo, estado) VALUES (4, 5, 2, '2026-07-09 09:30:00', 'Control de peso y dieta', 'CONFIRMADA');
INSERT INTO citas (id_cliente, id_mascota, id_veterinario, fecha_hora, motivo, estado) VALUES (5, 6, 2, '2026-07-10 14:00:00', 'Vacunacion inicial cachorra', 'AGENDADA');

-- ============================================================
-- 8. MS VENTAS — vetnova_ventas
-- ============================================================
USE vetnova_ventas;

DELETE FROM ventas;

INSERT INTO ventas (id_cliente, id_producto, cantidad, monto_total, estado, fecha_venta) VALUES (1, 2, 2, 17000.0, 'PAGADA', '2026-05-10 09:30:00');
INSERT INTO ventas (id_cliente, id_producto, cantidad, monto_total, estado, fecha_venta) VALUES (1, 4, 1, 28000.0, 'PAGADA', '2026-05-10 09:35:00');
INSERT INTO ventas (id_cliente, id_producto, cantidad, monto_total, estado, fecha_venta) VALUES (2, 3, 1, 12000.0, 'PAGADA', '2026-06-01 11:45:00');
INSERT INTO ventas (id_cliente, id_producto, cantidad, monto_total, estado, fecha_venta) VALUES (3, 5, 1, 18000.0, 'PAGADA', '2026-04-20 11:30:00');
INSERT INTO ventas (id_cliente, id_producto, cantidad, monto_total, estado, fecha_venta) VALUES (4, 9, 1, 11000.0, 'PAGADA', '2026-05-28 10:00:00');
INSERT INTO ventas (id_cliente, id_producto, cantidad, monto_total, estado, fecha_venta) VALUES (5, 1, 1, 15000.0, 'PENDIENTE', '2026-06-10 14:45:00');

-- ============================================================
-- 9. MS ATENCION CLINICA — vetnova_atencion_clinica
-- ============================================================
USE vetnova_atencion_clinica;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM diagnosticos;
DELETE FROM fichas_clinicas;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO fichas_clinicas (id_mascota, fecha_creacion, observaciones) VALUES (1, '2026-01-10 08:00:00', 'Mascota con seguimiento preventivo regular. Sin antecedentes graves.');
INSERT INTO fichas_clinicas (id_mascota, fecha_creacion, observaciones) VALUES (2, '2026-02-15 09:30:00', 'Castracion realizada sin complicaciones.');
INSERT INTO fichas_clinicas (id_mascota, fecha_creacion, observaciones) VALUES (3, '2026-03-20 10:00:00', 'Tratamiento dermatologico en curso. Responde bien a la medicacion.');
INSERT INTO fichas_clinicas (id_mascota, fecha_creacion, observaciones) VALUES (4, '2026-04-20 11:00:00', 'Primera atencion. Paciente sana.');
INSERT INTO fichas_clinicas (id_mascota, fecha_creacion, observaciones) VALUES (5, '2026-05-28 09:00:00', 'Control de peso. Dieta indicada.');
INSERT INTO fichas_clinicas (id_mascota, fecha_creacion, observaciones) VALUES (6, '2026-06-10 14:00:00', 'Primera vacuna aplicada. Proxima en 21 dias.');

INSERT INTO diagnosticos (descripcion, tratamiento, receta_medica, detalle_certificado, fecha, id_veterinario, id_ficha) VALUES ('Control preventivo anual. Sin hallazgos patologicos.', 'Vacuna triple canina reforzada. Antiparasitario oral administrado.', 'Milbemax 2.5mg - 1 comprimido oral unico', NULL, '2026-05-10 09:15:00', 2, 1);
INSERT INTO diagnosticos (descripcion, tratamiento, receta_medica, detalle_certificado, fecha, id_veterinario, id_ficha) VALUES ('Post-operatorio castracion sin complicaciones.', 'Antibiotico oral 7 dias. Collar isabelino hasta retiro de puntos.', 'Amoxicilina 250mg - 1 comprimido cada 12 horas por 7 dias', 'Se certifica que el animal fue esterilizado el 2026-04-22 en VetNova.', '2026-04-22 10:45:00', 2, 2);
INSERT INTO diagnosticos (descripcion, tratamiento, receta_medica, detalle_certificado, fecha, id_veterinario, id_ficha) VALUES ('Dermatitis alergica leve. Probable origen alimentario.', 'Cambio de dieta hipoalergenica. Shampoo dermatologico 2 veces por semana.', 'Apoquel 5.4mg - 1 comprimido cada 24 horas por 14 dias', NULL, '2026-06-01 11:30:00', 2, 3);
INSERT INTO diagnosticos (descripcion, tratamiento, receta_medica, detalle_certificado, fecha, id_veterinario, id_ficha) VALUES ('Primera consulta. Felino joven sano. Sin hallazgos.', 'Vacunacion inicial triple felina. Control en 21 dias.', NULL, NULL, '2026-04-20 11:20:00', 2, 4);
INSERT INTO diagnosticos (descripcion, tratamiento, receta_medica, detalle_certificado, fecha, id_veterinario, id_ficha) VALUES ('Sobrepeso leve. Sin patologia subyacente.', 'Dieta baja en calorias. Ejercicio diario 30 minutos.', NULL, NULL, '2026-05-28 09:45:00', 2, 5);
INSERT INTO diagnosticos (descripcion, tratamiento, receta_medica, detalle_certificado, fecha, id_veterinario, id_ficha) VALUES ('Cachorra en buen estado general. Primera vacuna sextuple aplicada.', 'Reposo relativo 48 horas. Evitar contacto con otros animales no vacunados.', NULL, NULL, '2026-06-10 14:30:00', 2, 6);

-- ============================================================
-- 10. MS NOTIFICACIONES — vetnova_notificaciones
-- NOTA: usa SEQUENCE para el ID — se inserta con ID explícito
--       para evitar conflicto con la secuencia al arrancar en 0
-- ============================================================
USE vetnova_notificaciones;

DELETE FROM notificaciones;

INSERT INTO notificaciones (id_notificacion, destinatario, mensaje, tipo, canal, prioridad, estado) VALUES (1, 'cliente1@vetnova.cl', 'Bienvenido a VetNova. Su cuenta ha sido registrada con exito.', 'CLIENTE', 'EMAIL', 'MEDIA', 'ENVIADA');
INSERT INTO notificaciones (id_notificacion, destinatario, mensaje, tipo, canal, prioridad, estado) VALUES (2, 'cliente2@vetnova.cl', 'Bienvenido a VetNova. Su cuenta ha sido registrada con exito.', 'CLIENTE', 'EMAIL', 'MEDIA', 'ENVIADA');
INSERT INTO notificaciones (id_notificacion, destinatario, mensaje, tipo, canal, prioridad, estado) VALUES (3, 'cliente1@vetnova.cl', 'Su mascota Max tiene una cita confirmada para el 2026-07-05 a las 09:00.', 'CITA_AGENDADA', 'EMAIL', 'ALTA', 'ENVIADA');
INSERT INTO notificaciones (id_notificacion, destinatario, mensaje, tipo, canal, prioridad, estado) VALUES (4, 'cliente2@vetnova.cl', 'Su mascota Rocky tiene una cita agendada para el 2026-07-07 a las 11:00.', 'CITA_AGENDADA', 'EMAIL', 'ALTA', 'ENVIADA');
INSERT INTO notificaciones (id_notificacion, destinatario, mensaje, tipo, canal, prioridad, estado) VALUES (5, 'admin@vetnova.cl', 'ALERTA: El producto Guantes de Latex tiene stock bajo (15 unidades). Minimo requerido: 5.', 'STOCK_BAJO', 'EMAIL', 'ALTA', 'ENVIADA');
INSERT INTO notificaciones (id_notificacion, destinatario, mensaje, tipo, canal, prioridad, estado) VALUES (6, 'cliente1@vetnova.cl', 'Se ha emitido una receta medica para su mascota Max.', 'ATENCION', 'EMAIL', 'MEDIA', 'ENVIADA');
INSERT INTO notificaciones (id_notificacion, destinatario, mensaje, tipo, canal, prioridad, estado) VALUES (7, 'admin@vetnova.cl', 'Recordatorio: Citas programadas para manana 2026-07-05: 2 citas confirmadas.', 'CITA_AGENDADA', 'EMAIL', 'BAJA', 'PENDIENTE');

-- Avanzar la secuencia para que el microservicio no colisione al crear nuevas notificaciones
ALTER TABLE notificaciones AUTO_INCREMENT = 100;

-- ============================================================
-- FIN DEL SEED
-- ============================================================
SELECT 'VetNova seed v2 completado exitosamente.' AS resultado;
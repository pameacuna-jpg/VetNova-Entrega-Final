-- Datos iniciales para MS Auth (Tabla: auth_usuarios)
-- La contraseña para todos estos usuarios de prueba es: 123456
-- (El hash corresponde a '123456' encriptado con BCrypt, que es lo que espera tu SecurityConfig)

INSERT INTO auth_usuarios (email, password, rol, activo, fecha_creacion) VALUES ('admin@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'ADMIN', true, CURRENT_TIMESTAMP);
INSERT INTO auth_usuarios (email, password, rol, activo, fecha_creacion) VALUES ('veterinario@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'VETERINARIO', true, CURRENT_TIMESTAMP);
INSERT INTO auth_usuarios (email, password, rol, activo, fecha_creacion) VALUES ('recepcion@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'RECEPCIONISTA', true, CURRENT_TIMESTAMP);
INSERT INTO auth_usuarios (email, password, rol, activo, fecha_creacion) VALUES ('cliente@vetnova.cl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjQgIQJ0xq', 'CLIENTE', true, CURRENT_TIMESTAMP);
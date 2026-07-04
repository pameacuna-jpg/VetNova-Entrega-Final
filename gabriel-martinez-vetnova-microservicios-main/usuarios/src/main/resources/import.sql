-- 1. Insertar primero los Roles base (id_rol se autogenera: 1 y 2 por ser IDENTITY)
INSERT INTO roles (nombre_rol, descripcion) VALUES ('VETERINARIO', 'Personal médico veterinario de la clínica');
INSERT INTO roles (nombre_rol, descripcion) VALUES ('ADMIN', 'Administrador global del sistema');

-- 2. Insertar los Permisos basados estrictamente en tu modelo real (id_permiso se autogenera)
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('CITAS_AGENDAR', 'AGENDA');
INSERT INTO permisos (nombre_permiso, modulo) VALUES ('ATENCION_REGISTRAR', 'CLINICA');

-- 3. Asociar Roles con Permisos (Tabla de unión @ManyToMany generada por la anotación @JoinTable en Rol)
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1, 1); -- El Rol 1 (VETERINARIO) tiene el Permiso 1
INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (1, 2); -- El Rol 1 (VETERINARIO) tiene el Permiso 2

-- 4. Insertar el Usuario (id_usuario se autogenera como 1 por ser IDENTITY)
-- Apunta a la id_sucursal 5 (que ya dejamos creada en el script de Sucursales)
INSERT INTO usuarios (nombre, email, password, estado, id_sucursal) 
VALUES ('Dr. Daniel Castro', 'daniel.doctor@vetnova.cl', '$2a$10$X5v123...hashSimulado', 'ACTIVE', 5);

-- 5. Asignar los Roles al Usuario a través de la entidad intermedia UsuarioRol
-- Vincula id_usuario = 1 con id_rol = 1 (VETERINARIO), con la fecha de asignación obligatoria
INSERT INTO usuarios_roles (id_usuario, id_rol, fecha_asignacion) VALUES (1, 1, '2026-01-01 08:00:00');@
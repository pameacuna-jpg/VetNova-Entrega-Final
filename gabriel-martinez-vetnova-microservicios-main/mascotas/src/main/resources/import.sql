-- ============================================================================
-- 1. POBLAR ESPECIES (id_especie se autogenerará como 1 y 2 por ser IDENTITY)
-- ============================================================================
INSERT INTO especies (nombre, descripcion) VALUES ('Canino', 'Perros domésticos de cualquier raza');
INSERT INTO especies (nombre, descripcion) VALUES ('Felino', 'Gatos domésticos de cualquier raza');


-- ============================================================================
-- 2. POBLAR MASCOTAS (id_mascota se autogenerará como 1, 2 y 3)
-- ============================================================================
-- Firulais (id_mascota = 1): Perro, Macho, pertenece al cliente 1 (Pamela)
INSERT INTO mascotas (nombre, raza, sexo, fecha_nacimiento, id_cliente, estado, numero_microchip, id_especie) 
VALUES ('Firulais', 'Quiltro', 'Macho', '2023-04-15', 1, 'ACTIVO', '900111222333441', 1);

-- Luna (id_mascota = 2): Gata, Hembra, pertenece al cliente 1 (Pamela)
INSERT INTO mascotas (nombre, raza, sexo, fecha_nacimiento, id_cliente, estado, numero_microchip, id_especie) 
VALUES ('Luna', 'Romana', 'Hembra', '2024-08-20', 1, 'ACTIVO', '900111222333442', 2);

-- Max (id_mascota = 3): Perro, Macho, pertenece al cliente 2 (Gabriel)
INSERT INTO mascotas (nombre, raza, sexo, fecha_nacimiento, id_cliente, estado, numero_microchip, id_especie) 
VALUES ('Max', 'Pastor Aleman', 'Macho', '2021-01-10', 2, 'ACTIVO', '900111222333443', 1);


-- ============================================================================
-- 3. POBLAR HISTORIALES DE MASCOTA (Asociados a id_mascota = 1, 2 y 3)
-- ============================================================================
INSERT INTO historiales_mascota (numero_historia_clinica, resumen_clinico, fecha_ultima_atencion, ultimo_peso, esta_esterilizado, alergias_criticas, id_mascota) 
VALUES ('HC-2023-001', 'Paciente sano, vacunas octuple al dia.', '2026-06-15', 14.5, true, 'Ninguna conocida', 1);

INSERT INTO historiales_mascota (numero_historia_clinica, resumen_clinico, fecha_ultima_atencion, ultimo_peso, esta_esterilizado, alergias_criticas, id_mascota) 
VALUES ('HC-2024-042', 'Control felino sano. Alergia menor a pulgas.', '2026-05-10', 3.2, true, 'Picadura de pulga', 2);

INSERT INTO historiales_mascota (numero_historia_clinica, resumen_clinico, fecha_ultima_atencion, ultimo_peso, esta_esterilizado, alergias_criticas, id_mascota) 
VALUES ('HC-2021-105', 'Tratamiento por otitis terminado con exito.', '2026-03-22', 32.1, false, 'Penicilina', 3);


-- ============================================================================
-- 4. POBLAR HISTORIAL DE TRANSFERENCIAS (Opcional, simulando un cambio antiguo)
-- ============================================================================
-- Simula que Max (id_mascota = 3) pertenecía antes a Juan Pérez (ID 3 en Clientes) y fue transferido a Gabriel (ID 2)
INSERT INTO transferencias_propietario (id_mascota, id_cliente_anterior, id_cliente_nuevo, fecha_transferencia) 
VALUES (3, 3, 2, '2025-11-30 16:00:00');
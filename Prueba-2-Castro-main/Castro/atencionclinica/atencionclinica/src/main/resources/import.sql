-- ============================================================
-- MS Atención Clínica (Puerto 8083) — vetnova_atencion_clinica
-- Tablas: fichas_clinicas, diagnosticos
-- id_mascota = referencia lógica al MS Mascotas
-- id_veterinario = referencia lógica al MS Usuarios (id=2, Dra. Sofia)
-- ============================================================

-- 1. Fichas clínicas (una por mascota)
INSERT INTO fichas_clinicas (id_mascota, fecha_creacion, observaciones) VALUES
  (1, '2026-01-10 08:00:00', 'Mascota con seguimiento preventivo regular. Sin antecedentes graves.'),
  (2, '2026-02-15 09:30:00', 'Castración realizada sin complicaciones.'),
  (3, '2026-03-20 10:00:00', 'Tratamiento dermatológico en curso. Responde bien a la medicación.'),
  (4, '2026-04-20 11:00:00', 'Primera atención. Paciente sana.'),
  (5, '2026-05-28 09:00:00', 'Control de peso. Dieta indicada.'),
  (6, '2026-06-10 14:00:00', 'Primera vacuna aplicada. Próxima en 21 días.');

-- 2. Diagnósticos por ficha
INSERT INTO diagnosticos (descripcion, tratamiento, receta_medica, detalle_certificado, fecha, id_veterinario, id_ficha) VALUES
  ('Control preventivo anual. Sin hallazgos patológicos.',
   'Vacuna triple canina reforzada. Antiparasitario oral administrado.',
   'Milbemax 2,5mg/25mg - 1 comprimido oral único',
   NULL,
   '2026-05-10 09:15:00', 2, 1),

  ('Post-operatorio castración sin complicaciones. Herida en buen estado.',
   'Antibiótico oral 7 días. Collar isabelino hasta retiro de puntos.',
   'Amoxicilina 250mg - 1 comprimido cada 12 horas por 7 días',
   'Se certifica que el animal fue esterilizado el 2026-04-22 en VetNova.',
   '2026-04-22 10:45:00', 2, 2),

  ('Dermatitis alérgica leve. Probable origen alimentario.',
   'Cambio de dieta hipoalergénica. Shampoo dermatológico 2 veces por semana.',
   'Apoquel 5,4mg - 1 comprimido cada 24 horas por 14 días',
   NULL,
   '2026-06-01 11:30:00', 2, 3),

  ('Primera consulta. Felino joven sano. Sin hallazgos.',
   'Vacunación inicial triple felina. Control en 21 días.',
   NULL,
   NULL,
   '2026-04-20 11:20:00', 2, 4),

  ('Sobrepeso leve (34kg, raza: ideal 28-32kg). Sin patología subyacente.',
   'Dieta baja en calorías. Ejercicio diario 30 minutos. Control en 3 meses.',
   NULL,
   NULL,
   '2026-05-28 09:45:00', 2, 5),

  ('Cachorra en buen estado general. Primera vacuna séxtuple aplicada.',
   'Reposo relativo 48 horas. Evitar contacto con otros animales no vacunados.',
   NULL,
   NULL,
   '2026-06-10 14:30:00', 2, 6);

-- ============================================================
-- MS Agenda (Puerto 8086) — vetnova_agenda
-- Tabla: citas
-- id_cliente / id_mascota / id_veterinario = referencias lógicas
-- id_veterinario 2 = Dra. Sofia Ramírez (usuario del MS Usuarios)
-- ============================================================

INSERT INTO citas (id_cliente, id_mascota, id_veterinario, fecha_hora, motivo, estado) VALUES
  (1, 1, 2, '2026-07-05 09:00:00', 'Control anual y vacunación',        'CONFIRMADA'),
  (1, 2, 2, '2026-07-06 10:30:00', 'Revisión post-castración',          'AGENDADA'),
  (2, 3, 2, '2026-07-07 11:00:00', 'Revisión dermatitis recurrente',    'AGENDADA'),
  (3, 4, 2, '2026-07-08 15:00:00', 'Primera consulta general',          'AGENDADA'),
  (4, 5, 2, '2026-07-09 09:30:00', 'Control de peso y dieta',           'CONFIRMADA'),
  (5, 6, 2, '2026-07-10 14:00:00', 'Vacunación inicial (cachorra)',     'AGENDADA');

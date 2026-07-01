-- ============================================================
-- MS Ventas (Puerto 8088) — vetnova_ventas
-- Tabla: ventas
-- id_cliente / id_producto = referencias lógicas a otros MS
-- ============================================================

INSERT INTO ventas (id_cliente, id_producto, cantidad, monto_total, estado, fecha_venta) VALUES
  (1, 2, 2, 17000.0, 'PAGADA',    '2026-05-10 09:30:00'),
  (1, 4, 1, 28000.0, 'PAGADA',    '2026-05-10 09:35:00'),
  (2, 3, 1, 12000.0, 'PAGADA',    '2026-06-01 11:45:00'),
  (3, 5, 1, 18000.0, 'PAGADA',    '2026-04-20 11:30:00'),
  (4, 9, 1, 11000.0, 'PAGADA',    '2026-05-28 10:00:00'),
  (5, 1, 1, 15000.0, 'PENDIENTE', '2026-06-10 14:45:00');

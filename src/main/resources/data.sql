INSERT INTO "user" (name, email, password, phone_number, role_code, created_at)
VALUES
  ('kim', 'kim1@test.com', 'pw1', '010-1111-1111', 0, TIMESTAMP '2025-06-15 10:00:00'),
  ('admin', 'kim@test.com', 'pw2', '010-2222-2222', 1, TIMESTAMP '2025-06-15 10:01:00'),
  ('superadmin', 'suadmin@test.com', 'pw3', '010-3333-3333', 2, TIMESTAMP '2025-06-15 10:02:00');


-- region (여행 지역)
INSERT INTO region (region_id, name, level, parent_id) VALUES (1, '서울', 1, NULL);

-- user (유저)
INSERT INTO "user" (name, email, password, phone_number, role_code, created_at)
VALUES
  ('kim', 'kim1@test.com', 'pw1', '010-1111-1111', 0, TIMESTAMP '2025-06-15 10:00:00'),
  ('admin', 'kim@test.com', 'pw2', '010-2222-2222', 1, TIMESTAMP '2025-06-15 10:01:00'),
  ('superadmin', 'suadmin@test.com', 'pw3', '010-3333-3333', 2, TIMESTAMP '2025-06-15 10:02:00');

-- product (상품)
INSERT INTO product (product_id, name, description, price, total_quantity, stock_quantity, sale_status, type, duration, region_region_id, created_at, updated_at)
VALUES
  (1, '제주도 2박3일 패키지', '제주도에서 즐기는 2박3일 일정', 250000, 30, 25, 1, 1, 3, 1, NOW(), NOW());

-- order (주문)
INSERT INTO "order" (order_id, user_id, order_date, status, total_quantity, created_at, updated_at)
VALUES
  (1001, 1, '2025-06-05 11:00:00', 'PENDING', 2, '2025-06-05 11:00:00', '2025-06-05 11:30:00'),
  (1002, 1, '2025-06-03 14:12:00', 'PENDING', 1, '2025-06-03 14:12:00', '2025-06-03 14:13:00');

-- order_item (주문 아이템)
INSERT INTO order_item (order_item_id, order_id, product_id, people_count, start_date, created_at, updated_at)
VALUES
  (1, 1001, 1, 2, '2025-06-05', NOW(), NOW()),
  (2, 1002, 1, 1, '2025-06-03', NOW(), NOW());

-- payment
-- payment (결제)
INSERT INTO payment (
  payment_id, order_id, payment_key, method, card_number, account_number, bank, mobile_phone, status, paid_at
) VALUES
  (1, 1001, 'paykey-abc123', 'CARD', '1234-5678-9876-5432', NULL, NULL, '010-1111-1111', 'PAID', '2025-06-05 11:01:00'),
  (2, 1002, 'paykey-def456', 'ACCOUNT_TRANSFER', NULL, '123456-78-901234', '신한은행', '010-1111-1111', 'PAID', '2025-06-03 14:14:00');


---- region
---- 국가
INSERT INTO region (name, level, parent_id) VALUES ('대한민국', 0, NULL);
---- 광역시/도 (level=1, parent_id는 NULL)
INSERT INTO region (name, level, parent_id) VALUES ('서울특별시', 1, 1);
INSERT INTO region (name, level, parent_id) VALUES ('경기도', 1, 1);
INSERT INTO region (name, level, parent_id) VALUES ('부산광역시', 1, 1);
---- 시군구 (level=2, parent_id는 상위 광역시/도의 region_id)
---- 실제로는 위 INSERT문의 결과로 생성된 값을 참조해야 함
INSERT INTO region (name, level, parent_id) VALUES ('강남구', 2, 2);
INSERT INTO region (name, level, parent_id) VALUES ('서초구', 2, 2);
INSERT INTO region (name, level, parent_id) VALUES ('종로구', 2, 2);
INSERT INTO region (name, level, parent_id) VALUES ('수원시', 2, 3);
INSERT INTO region (name, level, parent_id) VALUES ('용인시', 2, 3);
INSERT INTO region (name, level, parent_id) VALUES ('성남시', 2, 3);
INSERT INTO region (name, level, parent_id) VALUES ('해운대구', 2, 4);
INSERT INTO region (name, level, parent_id) VALUES ('동래구', 2, 4);
INSERT INTO region (name, level, parent_id) VALUES ('부산진구', 2, 4);

-- product
INSERT INTO products (name, price, total_quantity, stock_quantity, description, sale_status, type, duration, region_id, created_at, updated_at)
VALUES
('서울 2박 3일 자유여행', 199000, 100, 99, '서울의 명소를 자유롭게 즐길 수 있는 여행 상품입니다.', 1, 1, 3, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('부산 야경 & 해산물 투어', 299000, 80, 70, '부산의 아름다운 야경과 신선한 해산물을 만끽할 수 있는 투어.', 0, 1, 4, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('제주도 한라산 트레킹 패키지', 359000, 60, 50, '한라산 등반과 힐링 숙소가 포함된 액티비티 중심의 여행입니다.', 1,2, 5, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
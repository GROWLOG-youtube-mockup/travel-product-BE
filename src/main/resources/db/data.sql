-- region
-- 국가
INSERT INTO region (region_id, name, level, parent_id) VALUES (0, '대한민국', 0, NULL);
-- 광역시/도 (level=1, parent_id는 NULL)
INSERT INTO region (name, level, parent_id) VALUES ('서울특별시', 1, 0);
INSERT INTO region (name, level, parent_id) VALUES ('경기도', 1, 0);
INSERT INTO region (name, level, parent_id) VALUES ('부산광역시', 1, 0);

-- 시군구 (level=2, parent_id는 상위 광역시/도의 region_id)
-- 실제로는 위 INSERT문의 결과로 생성된 값을 참조해야 함
-- 예시에서는 서울(1), 경기(2), 부산(3)으로 가정
INSERT INTO region (name, level, parent_id) VALUES ('강남구', 2, 1);
INSERT INTO region (name, level, parent_id) VALUES ('서초구', 2, 1);
INSERT INTO region (name, level, parent_id) VALUES ('종로구', 2, 1);
INSERT INTO region (name, level, parent_id) VALUES ('수원시', 2, 2);
INSERT INTO region (name, level, parent_id) VALUES ('용인시', 2, 2);
INSERT INTO region (name, level, parent_id) VALUES ('성남시', 2, 2);
INSERT INTO region (name, level, parent_id) VALUES ('해운대구', 2, 3);
INSERT INTO region (name, level, parent_id) VALUES ('동래구', 2, 3);
INSERT INTO region (name, level, parent_id) VALUES ('부산진구', 2, 3);

-- product
INSERT INTO product (name, price, total_quantity, stock_quantity, description, sale_status, type, duration, region_region_id,created_at, updated_at)
VALUES
('서울 2박 3일 자유여행', 199000, 100, 85, '서울의 명소를 자유롭게 즐길 수 있는 여행 상품입니다.', 1, 0, 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('부산 야경 & 해산물 투어', 299000, 80, 65, '부산의 아름다운 야경과 신선한 해산물을 만끽할 수 있는 투어.', 1, 1, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('제주도 한라산 트레킹 패키지', 359000, 60, 55, '한라산 등반과 힐링 숙소가 포함된 액티비티 중심의 여행입니다.', 1, 4, 3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- product_image
INSERT INTO product_image (product_id, image_url) VALUES
(1, 'https://cdn.example.com/domestic/seoul-1.jpg'),
(1, 'https://cdn.example.com/domestic/seoul-2.jpg'),
(2, 'https://cdn.example.com/domestic/busan-1.jpg'),
(2, 'https://cdn.example.com/domestic/busan-2.jpg'),
(3, 'https://cdn.example.com/domestic/jeju-1.jpg'),
(3, 'https://cdn.example.com/domestic/jeju-2.jpg');

-- product_description_group
INSERT INTO product_description_group (product_id, title, type, sort_order, created_at, updated_at) VALUES
(1, '포함 사항', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '불포함 사항', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '코스 안내', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '준비물 안내', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- product_description_item
INSERT INTO product_description_item (group_id, content, sort_order, created_at, updated_at) VALUES
(1, '서울 시내 호텔 2박 숙박', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, '1일차 관광지 입장권', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '개별 식사 및 교통비', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '광안리 해변 → 자갈치 시장 → 해운대 야경', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '등산화, 바람막이, 개인 간식', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
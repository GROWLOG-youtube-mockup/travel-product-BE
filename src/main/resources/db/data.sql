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

INSERT INTO product_images (product_id, image_url, created_at) VALUES
--(1, 'https://growlog-travel-service.s3.ap-northeast-2.amazonaws.com/product-images/0e18e0bb-a6bf-4bf0-9ea8-7de103f5e57c-%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5+1.jpg', CURRENT_TIMESTAMP),
(1, 'https://example-bucket.s3.amazonaws.com/product-images/1_sub.jpg', CURRENT_TIMESTAMP),
(2, 'https://example-bucket.s3.amazonaws.com/product-images/2_main.jpg', CURRENT_TIMESTAMP),
(3, 'https://example-bucket.s3.amazonaws.com/product-images/3_main.jpg', CURRENT_TIMESTAMP),
(3, 'https://example-bucket.s3.amazonaws.com/product-images/3_sub.jpg', CURRENT_TIMESTAMP);

INSERT INTO product_description_group (title, type, sort_order, product_id) VALUES
('포함 사항', 0, 1, 1),
('불포함 사항', 1, 2, 1),
('코스 안내', 2, 1, 2),
('준비물 안내', 2, 1, 3);

INSERT INTO product_description_item (group_id, content, sort_order) VALUES
(1, '서울 시내 호텔 2박 숙박', 1),
(1, '1일차 관광지 입장권', 2),
(2, '개별 식사 및 교통비', 1),
(3, '광안리 해변 → 자갈치 시장 → 해운대 야경', 1),
(4, '등산화, 바람막이, 개인 간식', 1);
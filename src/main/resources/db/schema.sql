-- 기본 설정
-- -----------------------------------------------------
-- Schema travel_service
-- -----------------------------------------------------


-- 외래키 체크 끔
SET REFERENTIAL_INTEGRITY FALSE;
-- 테이블 삭제
DROP TABLE IF EXISTS
    region,
    cart_item,
    product_description_item,
    product_description_group,
    product_images,
    products,
    user;
-- 외래키 체크 켬
SET REFERENTIAL_INTEGRITY TRUE;

---- 1. 지역 테이블
CREATE TABLE region (
  region_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(45) NOT NULL,
  level TINYINT NOT NULL, -- '0: 국가, 1: 광역시/도, 2: 시/군/구'
  parent_id BIGINT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_parent_region
        FOREIGN KEY (parent_id)
        REFERENCES region(region_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- 2. 사용자 테이블
CREATE TABLE user (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20) NOT NULL UNIQUE,
  role_code TINYINT NOT NULL, -- '0: USER, 1: ADMIN, 2: SUPER_ADMIN',
  deleted_at DATETIME DEFAULT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 8. 상품 테이블
CREATE TABLE products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    total_quantity INT NOT NULL,
    stock_quantity INT NOT NULL,
    description TEXT,
    sale_status TINYINT NOT NULL,
    type TINYINT NOT NULL, -- '0: FREE, 1: PACKAGE, 2: SUMMER_VAC, 3: HISTORY, 4: ACTIVITY',
    duration INT NOT NULL,
    region_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_region
        FOREIGN KEY (region_id) REFERENCES region(region_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- 9. 상품 이미지
CREATE TABLE product_images (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(300) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_image_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- 10. 상품 설명 그룹
CREATE TABLE product_description_group (
  group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  type TINYINT NOT NULL, -- '0: 포함사항, 1: 불포함사항, 2: 기타',
  sort_order INT DEFAULT 0,
  product_id BIGINT NOT NULL,
  CONSTRAINT fk_desc_group_product FOREIGN KEY (product_id)
    REFERENCES products(product_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

-- 11. 상품 설명 항목
CREATE TABLE product_description_item (
  item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  group_id BIGINT NOT NULL,
  content VARCHAR(255) NOT NULL,
  sort_order INT DEFAULT 0,
  CONSTRAINT fk_desc_item_group FOREIGN KEY (group_id)
    REFERENCES product_description_group(group_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

-- 12. 장바구니 아이템Add commentMore actions
CREATE TABLE cart_item (
  cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  start_date DATE NOT NULL, -- '선택한 여행 시작일',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE (user_id, product_id, start_date),
  CONSTRAINT fk_cart_item_user FOREIGN KEY (user_id)
    REFERENCES user(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id)
    REFERENCES products(product_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);
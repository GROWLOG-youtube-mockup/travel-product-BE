-- 기본 설정
-- -----------------------------------------------------
-- Schema travel_service
-- -----------------------------------------------------


-- 외래키 체크 끔
SET REFERENTIAL_INTEGRITY FALSE;
-- 테이블 삭제
DROP TABLE IF EXISTS
    region,
    product_images,
  products;
-- 외래키 체크 켬
SET REFERENTIAL_INTEGRITY TRUE;

---- 1. 지역 테이블
CREATE TABLE region (
  region_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(45) NOT NULL,
  level TINYINT NOT NULL, -- '0: 국가, 1: 광역시/도, 2: 시/군/구'
  parent_id INT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_parent_region
        FOREIGN KEY (parent_id)
        REFERENCES region(region_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- 8. 상품 테이블
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    total_quantity INT NOT NULL,
    stock_quantity INT NOT NULL,
    description TEXT,
    sale_status TINYINT NOT NULL,
    type TINYINT NOT NULL, -- '0: FREE, 1: PACKAGE, 2: SUMMER_VAC, 3: HISTORY, 4: ACTIVITY',
    duration INT NOT NULL,
    region_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_region
        FOREIGN KEY (region_id) REFERENCES region(region_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- 9. 상품 이미지
CREATE TABLE product_images (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url VARCHAR(300) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_image_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
-- 기본 설정
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema travel_service
-- -----------------------------------------------------
USE travel_service;

-- 외래키 체크 끄기
SET FOREIGN_KEY_CHECKS = 0;

-- 테이블 삭제
DROP TABLE IF EXISTS
  product_description_item,
  product_description_group,
  product_image,
  payment,
  password_reset_request,
  order_item,
  `order`,
  cart_item,
  product,
  user_login_history,
  admin_action_log,
  user,
  email_verification,
  phone_verification,
  region;

-- 외래키 체크 다시 켜기
SET FOREIGN_KEY_CHECKS = 1;

-- 테이블 생성
-- 1. 지역 테이블
CREATE TABLE region (
  region_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(45) NOT NULL,
  level TINYINT NOT NULL,   // 1: 광역시/도, 2: 시군구
  parent_id INT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_parent_region
        FOREIGN KEY (parent_id)
        REFERENCES region(region_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 2. 사용자 테이블
CREATE TABLE user (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20) NOT NULL UNIQUE,
  role_code TINYINT NOT NULL COMMENT '0: USER, 1: ADMIN, 2: SUPER_ADMIN',
  deleted_at DATETIME DEFAULT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. 사용자 로그인 기록
CREATE TABLE user_login_history (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  login_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  ip_address VARCHAR(45),
  user_agent VARCHAR(255),
  CONSTRAINT fk_user_login_history_user FOREIGN KEY (user_id)
    REFERENCES user(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 4. 관리자 액션 로그
CREATE TABLE admin_action_log (
  log_id INT AUTO_INCREMENT PRIMARY KEY,
  action_type TINYINT NOT NULL COMMENT '0: PRODUCT_ADD, 1: ORDER_STATUS_CHANGE, 2: USER_MANAGE',
  target_type TINYINT NOT NULL COMMENT '0: PRODUCT, 1: ORDER, 2: USER',
  target_id INT NOT NULL,
  timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
  user_id INT NOT NULL,
  CONSTRAINT fk_admin_action_log_user FOREIGN KEY (user_id)
    REFERENCES user(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 5. 이메일 인증
CREATE TABLE email_verification (
  email VARCHAR(100) PRIMARY KEY,
  code VARCHAR(20) NOT NULL,
  verified TINYINT DEFAULT 0
) ENGINE=InnoDB;

-- 6. 전화번호 인증
CREATE TABLE phone_verification (
  phone_number VARCHAR(20) NOT NULL PRIMARY KEY,
  code VARCHAR(20) NOT NULL,
  verified TINYINT DEFAULT 0
) ENGINE=InnoDB;

-- 7. 비밀번호 재설정 요청
CREATE TABLE password_reset_request (
  request_id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(100) NOT NULL,
  temp_password VARCHAR(255) NOT NULL,
  issued_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  user_id INT NOT NULL,
  CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id)
    REFERENCES user(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 8. 상품 테이블
CREATE TABLE product (
  product_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  thumbnail_image_url VARCHAR(255),
  price INT NOT NULL,
  total_quantity INT NOT NULL,
  stock_quantity INT NOT NULL,
  description TEXT,
  sale_status TINYINT NOT NULL COMMENT '0: UPCOMING, 1: ON_SALE, 2: SOLD_OUT',
  type TINYINT NOT NULL COMMENT '0: FREE, 1: PACKAGE, 2: SUMMER_VAC, 3: HISTORY, 4: ACTIVITY',
  duration INT NOT NULL COMMENT '여행 기간 (일수)',
  region_region_id INT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_product_region FOREIGN KEY (region_region_id)
    REFERENCES region(region_id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 9. 상품 이미지
CREATE TABLE product_image (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  image_url VARCHAR(255) NOT NULL,
  CONSTRAINT fk_product_image_product FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 10. 상품 설명 그룹
CREATE TABLE product_description_group (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  type TINYINT NOT NULL COMMENT '0: 포함사항, 1: 불포함사항, 2: 기타',
  sort_order INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_desc_group_product FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 11. 상품 설명 항목
CREATE TABLE product_description_item (
  id INT AUTO_INCREMENT PRIMARY KEY,
  group_id INT NOT NULL,
  content VARCHAR(255) NOT NULL,
  sort_order INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_desc_item_group FOREIGN KEY (group_id)
    REFERENCES product_description_group(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 12. 장바구니 아이템
CREATE TABLE cart_item (
  cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL,
  start_date DATE NOT NULL COMMENT '선택한 여행 시작일',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE (user_id, product_id, start_date),
  CONSTRAINT fk_cart_item_user FOREIGN KEY (user_id)
    REFERENCES user(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 13. 주문 테이블
CREATE TABLE `order` (
  order_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  cancel_date DATETIME,
  total_quantity INT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_order_user FOREIGN KEY (user_id)
    REFERENCES user(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 14. 주문 아이템
CREATE TABLE order_item (
  order_item_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  people_count INT NOT NULL,
  start_date DATE NOT NULL COMMENT '선택한 여행 시작일',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_order_item_order FOREIGN KEY (order_id)
    REFERENCES `order`(order_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_order_item_product FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 15. 결제 테이블
CREATE TABLE payment (
  payment_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  card_number VARCHAR(30) NOT NULL,
  status TINYINT NOT NULL COMMENT '0: APPROVED, 1: CANCELED',
  payment_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_payment_order FOREIGN KEY (order_id)
    REFERENCES `order`(order_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 설정 복원
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

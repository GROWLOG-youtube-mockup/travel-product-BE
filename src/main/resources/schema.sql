DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS "order";
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS region;

CREATE TABLE region (
  region_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(45) NOT NULL,
  level INT NOT NULL,
  parent_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "user" (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20) NOT NULL UNIQUE,
  role_code TINYINT NOT NULL,
  deleted_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product (
  product_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  price INT NOT NULL,
  total_quantity INT NOT NULL,
  stock_quantity INT NOT NULL,
  sale_status TINYINT NOT NULL,
  type TINYINT NOT NULL,
  duration INT NOT NULL,
  region_region_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "order" (
  order_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  cancel_date TIMESTAMP,
  status VARCHAR(20) NOT NULL,
  total_quantity INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES "user"(user_id)
);

CREATE TABLE order_item (
  order_item_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  people_count INT NOT NULL,
  start_date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES "order"(order_id),
  FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE payment (
  payment_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  payment_key VARCHAR(100),
  method VARCHAR(30),
  card_number VARCHAR(30),
  account_number VARCHAR(30),
  bank VARCHAR(30),
  mobile_phone VARCHAR(20),
  status VARCHAR(20),
  paid_at TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES "order"(order_id)
);

CREATE TABLE admin_action_log (
  log_id INT AUTO_INCREMENT PRIMARY KEY,
  action_type TINYINT NOT NULL, -- 0: PRODUCT_ADD, 1: ORDER_STATUS_CHANGE, 2: USER_MANAGE
  target_id INT NOT NULL,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  user_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE CASCADE
);

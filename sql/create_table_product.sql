CREATE TABLE products
(
    product_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    thumbnail_image_url TEXT,
    price               INT          NOT NULL,
    total_quantity      INT          NOT NULL,
    stock_quantity      INT          NOT NULL,
    description         TEXT,
    sale_status         VARCHAR(20)  NOT NULL,
    duration            INT          NOT NULL,
    region_region_id    BIGINT,
    created_at          DATETIME,
    updated_at          DATETIME,
    FOREIGN KEY (region_region_id) REFERENCES regions (region_id)
);
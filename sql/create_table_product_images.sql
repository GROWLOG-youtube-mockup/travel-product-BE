CREATE TABLE product_images (
    image_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url TEXT NOT NULL,
    sort_order INT,
    product_id BIGINT,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
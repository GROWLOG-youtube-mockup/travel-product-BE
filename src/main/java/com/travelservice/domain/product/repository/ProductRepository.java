package com.travelservice.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

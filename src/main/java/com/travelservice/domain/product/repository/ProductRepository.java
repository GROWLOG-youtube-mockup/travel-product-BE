package com.travelservice.domain.product.repository;

import com.travelservice.domain.product.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

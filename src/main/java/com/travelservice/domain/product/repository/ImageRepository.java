package com.travelservice.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelservice.domain.product.entity.ProductImage;

@Repository
public interface ImageRepository extends JpaRepository<ProductImage, Integer> {
}

package com.travelservice.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.Region;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findAll();

	List<Product> findByRegion_RegionId(Long regionId);

	// 특정 부모 Region을 가진 하위 Region에 속한 상품 조회
	List<Product> findByRegion_Parent_RegionId(Long parentRegionId);

	// 특정 지역에 해당하는 상품 조회
	List<Product> findByRegionIn(List<Region> regions);
}

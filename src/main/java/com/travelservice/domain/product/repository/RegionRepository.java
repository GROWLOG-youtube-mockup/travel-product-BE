package com.travelservice.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelservice.domain.product.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {

	// 이름으로 조회
	Optional<Region> findByName(String name);

	// 부모 Region 기준으로 자식 Region 리스트 조회
	List<Region> findByParent(Region parent);

	// 특정 레벨의 지역만 조회 (예: level=1인 광역시/도만 조회)
	List<Region> findByLevel(Integer level);

}

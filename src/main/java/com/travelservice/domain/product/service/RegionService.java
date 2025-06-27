package com.travelservice.domain.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.product.dto.RegionResponse;
import com.travelservice.domain.product.entity.Region;
import com.travelservice.domain.product.repository.RegionRepository;

@Service
@Transactional
public class RegionService {

	private final RegionRepository regionRepository;

	public RegionService(RegionRepository regionRepository) {
		this.regionRepository = regionRepository;
	}

	// 이름으로 지역 조회
	public Region getRegionByName(String name) {
		return regionRepository.findByName(name)
			.orElseThrow(() -> new RuntimeException("Region not found"));
	}

	// 아이디로 지역 조회
	public Region getRegionById(Long regionId) {
		return regionRepository.findById(regionId)
			.orElseThrow(() -> new RuntimeException("Region not found"));
	}

	// 부모 지역(광역시/도) 기준으로 자식 지역 목록 조회
	public List<Region> getChildrenByParentId(Long parentId) {
		return regionRepository.findByParent_RegionId(parentId);
	}

	// 특정 레벨 지역만 조회
	public List<RegionResponse> getRegions(Integer level, Long parentId) {
		List<Region> regions = List.of();
		if (parentId != null) {
			regions = regionRepository.findByParent_RegionId(parentId);
		} else if (level != null) {
			regions = regionRepository.findByLevel(level);
		}
		return regions.stream()
			.map(RegionResponse::from)
			.collect(Collectors.toList());
	}

}

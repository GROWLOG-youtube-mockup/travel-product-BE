package com.travelservice.domain.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.travelservice.domain.admin.dto.product.AdminProductResponseDto;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.repository.ProductRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductService {
	private final ProductRepository productRepository;

	public PagedAdminProductListResponse getAdminProductList(Pageable pageable, Long regionId) {
		Page<Product> page;
		if (regionId != null) {
			page = productRepository.findByRegion_RegionId(regionId, pageable);
		} else {
			page = productRepository.findAll(pageable);
		}

		return new PagedAdminProductListResponse(
			page.map(this::toDto).getContent(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.getNumber() + 1 // 0부터 시작이므로 +1
		);
	}

	// 엔티티 → DTO 변환
	private AdminProductResponseDto toDto(Product product) {
		// 대표 이미지(썸네일)
		String thumbnail = null;
		if (product.getImages() != null && !product.getImages().isEmpty()) {
			thumbnail = product.getImages().get(0).getImageUrl();
		}

		// type, saleStatus를 String 변환 (필요시 enum 매핑)
		String typeStr = typeToString(product.getType());
		String saleStatusStr = saleStatusToString(product.getSaleStatus());

		// duration 변환 (예: 4박 5일)
		String durationStr =
			product.getDuration() != null ? product.getDuration() + "박 " + (product.getDuration() + 1) + "일" : null;

		return AdminProductResponseDto.builder()
			.productId(product.getProductId())
			.thumbnail(thumbnail)
			.name(product.getName())
			.region(AdminProductResponseDto.RegionDto.builder()
				.regionId(product.getRegion().getRegionId().intValue())
				.name(product.getRegion().getName())
				.build())
			.type(typeStr)
			.saleStatus(saleStatusStr)
			.price(product.getPrice())
			.stockQuantity(product.getStockQuantity())
			.duration(durationStr)
			.build();
	}

	private String typeToString(Integer type) {
		if (type == null) {
			return "";
		}
		switch (type) {
			case 0:
				return "자유여행";
			case 1:
				return "패키지";
			case 2:
				return "여름방학";
			case 3:
				return "역사";
			case 4:
				return "액티비티";
			default:
				return "기타";
		}
	}

	private String saleStatusToString(Integer saleStatus) {
		if (saleStatus == null) {
			return "";
		}
		switch (saleStatus) {
			case 0:
				return "ON_SALE";
			case 1:
				return "UP_COMING";
			case 2:
				return "END";
			default:
				return "UNKNOWN";
		}
	}

	@Getter
	public static class PagedAdminProductListResponse {
		private final java.util.List<AdminProductResponseDto> content;
		private final long totalElements;
		private final int totalPages;
		private final int currentPage;

		public PagedAdminProductListResponse(java.util.List<AdminProductResponseDto> content, long totalElements,
			int totalPages, int currentPage) {
			this.content = content;
			this.totalElements = totalElements;
			this.totalPages = totalPages;
			this.currentPage = currentPage;
		}

	}
}

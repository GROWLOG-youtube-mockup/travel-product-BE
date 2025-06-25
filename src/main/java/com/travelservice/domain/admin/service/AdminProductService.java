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
			thumbnail = product.getImages().getFirst().getImageUrl();
		}

		return AdminProductResponseDto.builder()
			.productId(product.getProductId())
			.thumbnail(thumbnail)
			.name(product.getName())
			.region(AdminProductResponseDto.RegionDto.builder()
				.regionId(product.getRegion().getRegionId().intValue())
				.name(product.getRegion().getName())
				.build())
			.type(product.getType())
			.saleStatus(product.getSaleStatus())
			.price(product.getPrice())
			.stockQuantity(product.getStockQuantity())
			.duration(product.getDuration())
			.build();
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

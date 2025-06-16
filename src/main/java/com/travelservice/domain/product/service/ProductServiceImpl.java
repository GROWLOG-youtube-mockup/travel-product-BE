package com.travelservice.domain.product.service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.ProductDetailResponse;
import com.travelservice.domain.product.dto.ProductListResponse;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductDescriptionGroup;
import com.travelservice.domain.product.entity.ProductDescriptionItem;
import com.travelservice.domain.product.entity.ProductImage;
import com.travelservice.domain.product.entity.Region;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.product.repository.RegionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final RegionRepository regionRepository;
	private final S3Uploader s3Uploader;

	@Override
	@Transactional
	public ProductDetailResponse createProduct(AddProductRequest request) throws IOException {
		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new IllegalArgumentException("지역을 찾을 수 없습니다" + request.getRegionId()));

		try {
			Product product = request.toEntity(region);
			Product savedProduct = productRepository.save(product);
			return ProductDetailResponse.from(savedProduct);
		} catch (DataIntegrityViolationException e) {
			throw new IllegalStateException("데이터 무결성 제약 조건 위반: " + e.getMessage(), e);
		} catch (DataAccessException e) {
			throw new RuntimeException("데이터베이스 접근 오류가 발생했습니다", e);
		} catch (Exception e) {
			throw new RuntimeException("상품 생성 중 예상치 못한 오류가 발생했습니다", e);
		}
	}

	@Override
	@Transactional
	public List<ProductListResponse> getAllProducts() {
		List<Product> products = productRepository.findAll();

		return products.stream()
			.map(ProductListResponse::new)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public ProductDetailResponse getProductDetail(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productId));

		return ProductDetailResponse.from(product);
	}

	@Override
	@Transactional
	public Product updateProduct(Long productId, UpdateProductRequest request) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new RuntimeException("지역을 찾을 수 없습니다 " + request.getRegionId()));

		// 필드 업데이트
		product.update(
			request.getName(),
			request.getPrice(),
			request.getTotalQuantity(),
			request.getStockQuantity(),
			request.getDescription(),
			request.getSaleStatus(),
			request.getType(),
			request.getDuration(),
			region
		);

		// 이미지 갱신
		product.clearImages();
		if (request.getImageUrls() != null) {
			for (String url : request.getImageUrls()) {
				product.addImage(ProductImage.builder().imageUrl(url).build());
			}
		}

		// 설명 그룹 갱신
		product.clearDescriptionGroups();
		if (request.getDescriptionGroups() != null) {
			// for (ProductDescriptionGroupRequest groupReq : request.getDescriptionGroups()) {
			// 	product.addDescriptionGroup(ProductDescriptionGroup.builder()
			// 		.title(groupReq.getTitle())
			// 		.type(groupReq.getType())
			// 		.sortOrder(groupReq.getSortOrder())
			// 		.build());
			// }
			request.getDescriptionGroups().forEach(groupReq -> {
				ProductDescriptionGroup group = ProductDescriptionGroup.builder()
					.title(groupReq.getTitle())
					.type(groupReq.getType())
					.sortOrder(groupReq.getSortOrder())
					.build();

				groupReq.getItems().forEach(itemReq -> {
					ProductDescriptionItem item = ProductDescriptionItem.builder()
						.content(itemReq.getContent())
						.sortOrder(itemReq.getSortOrder())
						.build();

					group.addItem(item);
				});

				product.addDescriptionGroup(group);
			});
		}
		return productRepository.save(product);
	}

	@Override
	@Transactional
	public void deleteProduct(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다: " + productId));

		productRepository.delete(product);
	}
}

package com.travelservice.domain.product.service;

import java.util.List;
import java.util.stream.Collectors;

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
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final RegionRepository regionRepository;
	private final S3Uploader s3Uploader;

	@Override
	@Transactional
	public ProductDetailResponse createProduct(AddProductRequest request) {
		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND));

		Product product = request.toEntity(region);
		Product savedProduct = productRepository.save(product);

		return ProductDetailResponse.from(savedProduct);
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
			.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

		return ProductDetailResponse.from(product);
	}

	@Override
	@Transactional
	public Product updateProduct(Long productId, UpdateProductRequest request) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND));

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
			.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

		productRepository.delete(product);
	}
}

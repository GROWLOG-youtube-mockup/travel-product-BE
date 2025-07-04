package com.travelservice.domain.product.service;

import static com.travelservice.global.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.entity.AdminActionLog;
import com.travelservice.domain.admin.repository.AdminActionLogRepository;
import com.travelservice.domain.admin.repository.AdminUserRepository;
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
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final RegionRepository regionRepository;
	private final S3Uploader s3Uploader;
	private final AdminUserRepository adminUserRepository;
	private final AdminActionLogRepository adminActionLogRepository;

	@Override
	@Transactional
	public ProductDetailResponse createProduct(AddProductRequest request) {
		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new CustomException(REGION_NOT_FOUND));

		Product product = request.toEntity(region);
		Product savedProduct = productRepository.save(product);

		// action-log INSERT
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long adminUserId = Long.valueOf(authentication.getName());
		User adminUser = adminUserRepository.findById(adminUserId)
			.orElseThrow(() -> new CustomException(AUTH_INFO_NOT_FOUND));

		AdminActionLog log = AdminActionLog.builder()
			.user(adminUser)
			.actionType(0) // 0: 상품 등록
			.targetId(savedProduct.getProductId())
			.timestamp(LocalDateTime.now())
			.build();
		adminActionLogRepository.save(log);

		return ProductDetailResponse.from(savedProduct);
	}

	@Override
	@Transactional
	public List<ProductListResponse> getAllProducts(Long regionId, Long parentRegionId, String tags) {
		List<Product> products;

		if (regionId != null) {
			products = productRepository.findByRegion_RegionId(regionId);
		} else if (parentRegionId != null) {
			List<Region> childRegions = regionRepository.findByParent_RegionId(parentRegionId);
			products = productRepository.findByRegionIn(childRegions);
		} else {
			products = productRepository.findAll();
		}

		// 태그로 필터링
		if (tags != null && !tags.isBlank()) {
			List<String> tagList = Arrays.stream(tags.split(","))
				.map(String::trim).toList();

			products = products.stream()
				.filter(product -> product.getDescriptionGroups().stream()
					.anyMatch(group -> group.getTitle().equalsIgnoreCase("tags")
						&& group.getDescriptionItems().stream()
						.anyMatch(item -> tagList.contains(item.getContent()))))
				.collect(Collectors.toList());
		}

		return products.stream()
			.map(ProductListResponse::from)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<ProductListResponse> getProductsByParentRegion(Long parentRegionId) {
		List<Product> products = productRepository.findByRegion_Parent_RegionId(parentRegionId);
		return products.stream().map(ProductListResponse::from).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public ProductDetailResponse getProductDetail(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		return ProductDetailResponse.from(product);
	}

	@Override
	@Transactional
	public Product updateProduct(Long productId, UpdateProductRequest request) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new CustomException(REGION_NOT_FOUND));

		// 필드 업데이트
		product.update(
			request.getName(),
			request.getPrice(),
			request.getTotalQuantity(),
			request.getStockQuantity(),
			request.getDescription(),
			request.getType(),
			request.getSaleStatus(),
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
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		productRepository.delete(product);
	}
}


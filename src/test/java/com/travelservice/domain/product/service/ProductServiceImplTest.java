package com.travelservice.domain.product.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.product.dto.ProductListResponse;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductDescriptionGroup;
import com.travelservice.domain.product.entity.ProductImage;
import com.travelservice.domain.product.entity.Region;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.product.repository.RegionRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

@SpringBootTest
@Transactional
class ProductServiceImplTest {
	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private RegionRepository regionRepository;

	@BeforeEach
	void setUp() {
		Region region = regionRepository.save(new Region("광주", 2, null));

		Product product = Product.builder()
			.name("테스트 상품")
			.price(10000)
			.totalQuantity(10)
			.stockQuantity(10)
			.description("테스트 설명")
			.saleStatus(1)
			.type(1)
			.duration(3)
			.region(region)
			.build();

		product.addImage(ProductImage.builder().imageUrl("https://image.com/test.jpg").build());
		product.addDescriptionGroup(ProductDescriptionGroup.builder().title("테스트 사항").type(0).sortOrder(1).build());

		productRepository.save(product);
	}

	@Test
	void getAllProducts_성공() {
		Region region = regionRepository.findByName("광주")
			.orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND));
		List<ProductListResponse> products = productService.getAllProducts(region.getRegionId());

		assertThat(products).isNotEmpty();
		assertThat(products.get(0).getName()).isEqualTo("테스트 상품");
		assertThat(products.get(0).getImageUrls().getFirst()).isEqualTo("https://image.com/test.jpg");
	}

}
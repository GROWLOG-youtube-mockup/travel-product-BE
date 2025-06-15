package com.travelservice.domain.product.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.ProductDescriptionGroupRequest;
import com.travelservice.domain.product.dto.ProductDetailResponse;
import com.travelservice.domain.product.dto.ProductListResponse;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductDescriptionGroup;
import com.travelservice.domain.product.entity.ProductImage;
import com.travelservice.domain.product.entity.Region;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.product.repository.RegionRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
@AutoConfigureMockMvc  // MockMvc 생성 및 자동 구성
class ProductControllerTest {

	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected ObjectMapper objectMapper; // 직렬화, 역 직렬화를 위한 클래스
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private RegionRepository regionRepository;

	@BeforeEach
	public void mockMvcSetUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.build();
		productRepository.deleteAll();
	}

	public Product productSetUp() {
		// given
		final String name = "테스트";
		final Integer price = 999;
		final Integer totalQuantity = 100;
		final Integer stockQuantity = 90;
		final String description = "테스트입니다";
		final Integer saleStatus = 0;
		final Integer type = 0;
		final Integer duration = 1;
		final Long regionId = 5L;

		Region region = regionRepository.findById(regionId)
			.orElseThrow(() -> new IllegalArgumentException("not found region"));

		Product savedProduct = Product.builder()
			.name(name)
			.price(price)
			.totalQuantity(totalQuantity)
			.stockQuantity(stockQuantity)
			.description(description)
			.saleStatus(saleStatus)
			.type(type)
			.duration(duration)
			.region(region)
			.build();

		savedProduct.addImage(ProductImage.builder()
			.imageUrl("https://example.com/images/test-image1.jpg")
			.build());

		savedProduct.addImage(ProductImage.builder()
			.imageUrl("https://example.com/images/test-image2.jpg")
			.build());

		ProductDescriptionGroup group1 = ProductDescriptionGroup.builder()
			.title("포함 사항")
			.type(0)
			.sortOrder(1)
			.build();
		ProductDescriptionGroup group2 = ProductDescriptionGroup.builder()
			.title("미포함 사항")
			.type(1)
			.sortOrder(2)
			.build();

		savedProduct.addDescriptionGroup(group1);
		savedProduct.addDescriptionGroup(group2);
		productRepository.save(savedProduct);

		return savedProduct;
	}

	@DisplayName("createProduct: 상품 추가에 성공한다")
	@Test
	public void createProduct() throws Exception {
		// given
		final String url = "/products";

		AddProductRequest request = AddProductRequest.builder()
			.name("테스트 상품 1")
			.price(1000)
			.totalQuantity(100)
			.stockQuantity(100)
			.description("상품 설명입니다")
			.saleStatus(0)
			.type(1)
			.duration(5)
			.regionId(5L)
			.imageUrls(List.of(
				"https://s3.amazonaws.com/.../img1.jpg",
				"https://s3.amazonaws.com/.../img2.jpg"
			))
			.descriptionGroups(List.of(
				new ProductDescriptionGroupRequest("포함 사항", 0, 1),
				new ProductDescriptionGroupRequest("미포함 사항", 1, 2)
			))
			.build();

		// 객체 JSON으로 직렬화
		final String requestBody = objectMapper.writeValueAsString(request);
		int beforeSize = productRepository.findAll().size();

		// when

		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());
	}

	@DisplayName("getAllProducts: 상품 목록 조회에 성공한다")
	@Test
	public void getAllProducts() throws Exception {
		// given
		final String url = "/products";
		Product savedProduct = productSetUp();
		int expectedSize = productRepository.findAll().size();

		// when
		final ResultActions resultActions = mockMvc.perform(get(url)
			.accept(APPLICATION_JSON_VALUE));

		// then: HTTP 상태
		resultActions.andExpect(status().isOk());

		// then: 응답 본문 파싱
		String responseBody = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
		System.out.println("Response JSON:\n" + responseBody);

		// JSON 배열 → DTO
		List<ProductListResponse> products = objectMapper.readValue(
			responseBody,
			new TypeReference<List<ProductListResponse>>() {
			}
		);

		// then: 개수 일치
		assertThat(products.size()).isEqualTo(expectedSize);

		// then: 저장한 상품이 포함되어 있는지 확인
		boolean found = products.stream().anyMatch(p ->
			p.getName().equals(savedProduct.getName()) &&
				p.getPrice().equals(savedProduct.getPrice())
		);

		assertThat(found).isTrue();
	}

	@DisplayName("getProductByID: 상품 상세 조회에 성공한다")
	@Test
	public void getProductById() throws Exception {
		// given
		final String url = "/products/{productId}";
		Product savedProduct = productSetUp();

		// when
		final ResultActions resultActions = mockMvc.perform(get(url, savedProduct.getProductId()));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(savedProduct.getName()))
			.andExpect(jsonPath("$.price").value(savedProduct.getPrice()))
			.andExpect(jsonPath("$.region.regionId").value(savedProduct.getRegion().getRegionId()))
			.andExpect(jsonPath("$.region.name").value(savedProduct.getRegion().getName()));
	}

	@DisplayName("deleteProduct: 상품 삭제에 성공한다")
	@Test
	public void deleteProduct() throws Exception {

		// given
		String url = "/products/{productId}";
		Product product = productSetUp();
		Long productId = product.getProductId();

		// when
		ResultActions resultActions = mockMvc.perform(delete(url, productId));

		// then
		resultActions.andExpect(status().isNoContent());

		// DB에 존재하지 않는지 확인
		Optional<Product> deleted = productRepository.findById(productId);
		assertThat(deleted).isEmpty();
	}

	@DisplayName("updateProduct: 상품 정보 수정에 성공한다")
	@Test
	public void updateProduct() throws Exception {
		// given
		final String url = "/products/{productId}";
		Product savedProduct = productSetUp();

		final String newName = "상품 정보 수정용 테스트";
		final Integer newPrice = 100;
		final List<String> newImageUrls = List.of(
			"https://s3.amazonaws.com/.../newImg1.jpg",
			"https://s3.amazonaws.com/.../newImg2.jpg"
		);
		List<ProductDescriptionGroupRequest> newDescriptionGroups = List.of(
			new ProductDescriptionGroupRequest("포함 사항", 0, 1),
			new ProductDescriptionGroupRequest("미포함 사항", 1, 2)
		);

		UpdateProductRequest request = UpdateProductRequest.builder()
			.name(newName)
			.price(savedProduct.getPrice())
			.totalQuantity(savedProduct.getTotalQuantity())
			.stockQuantity(savedProduct.getStockQuantity())
			.description(savedProduct.getDescription())
			.saleStatus(savedProduct.getSaleStatus())
			.type(savedProduct.getType())
			.duration(savedProduct.getDuration())
			.regionId(savedProduct.getRegion().getRegionId())
			.imageUrls(newImageUrls)
			.descriptionGroups(newDescriptionGroups)
			.build();

		// when
		ResultActions result = mockMvc.perform(put(url, savedProduct.getProductId())
			.contentType(APPLICATION_JSON_VALUE)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result.andExpect(status().isOk());
		String response = result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
		ProductDetailResponse responseDto = objectMapper.readValue(response, ProductDetailResponse.class);
		assertThat(responseDto.getName()).isEqualTo(newName);
		assertThat(responseDto.getDescriptionGroups().getFirst().getTitle()).isEqualTo("포함 사항");
	}
}
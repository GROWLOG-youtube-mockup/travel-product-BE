package com.travelservice.domain.product.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductImage;
import com.travelservice.domain.product.entity.Region;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.product.repository.RegionRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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

	@AfterEach
	public void cleanUp() {
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
		final Integer regionId = 5;

		Region region = regionRepository.findById(regionId)
			.orElseThrow(() -> new IllegalArgumentException("not found region"));

		Product savedProduct = productRepository.save(Product.builder()
			.name(name)
			.price(price)
			.totalQuantity(totalQuantity)
			.stockQuantity(stockQuantity)
			.description(description)
			.saleStatus(saleStatus)
			.type(type)
			.duration(duration)
			.region(region)
			.build());

		savedProduct.addImage(ProductImage.builder()
			.imageUrl("https://example.com/images/test-image1.jpg")
			.build());

		savedProduct.addImage(ProductImage.builder()
			.imageUrl("https://example.com/images/test-image2.jpg")
			.build());

		return savedProduct;
	}

	@DisplayName("createProduct: 상품 추가에 성공한다")
	@Test
	public void createProduct() throws Exception {
		// given
		final String url = "/products";
		Region region = regionRepository.findAll().get(5); // 유효한 지역 가져오기
		List<String> imageUrls = new ArrayList<>();
		imageUrls.add("https://s3.amazonaws.com/.../img1.jpg");
		imageUrls.add("https://s3.amazonaws.com/.../img2.jpg");

		final AddProductRequest userRequest = new AddProductRequest(
			"테스트 상품", 1000, 10, 10, "상품 설명입니다.",
			0, 0, 3, region.getRegionId(), imageUrls
		);

		// // 객체 JSON으로 직렬화
		final String requestBody = objectMapper.writeValueAsString(userRequest);

		int beforeSize = productRepository.findAll().size();

		// when

		// 설정한 내용을 바탕으로 요청 전송
		ResultActions result = mockMvc.perform(post(url)
			.contentType(APPLICATION_JSON_VALUE)
			.content(requestBody));

		// then
		result.andExpect(status().isCreated());

		List<Product> products = productRepository.findAll();
		assertThat(products.size()).isEqualTo(beforeSize + 1);

		Product lastProduct = products.get(products.size() - 1); // 가장 마지막 상품

		assertThat(lastProduct.getName()).isEqualTo("테스트 상품");
		assertThat(lastProduct.getPrice()).isEqualTo(1000);
		assertThat(lastProduct.getRegion().getRegionId()).isEqualTo(region.getRegionId());
	}

	@DisplayName("getAllProducts: 상품 목록 조회에 성공한다")
	@Test
	public void getAllProducts() throws Exception {
		// given
		final String url = "/products";
		int beforeSize = productRepository.findAll().size();
		Product savedProduct = productSetUp();

		// when
		final ResultActions resultActions = mockMvc.perform(get(url)
			.accept(APPLICATION_JSON_VALUE));

		// then: HTTP 상태
		resultActions.andExpect(status().isOk());

		// then: 응답 본문 파싱
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("Response JSON:\n" + responseBody);

		// JSON 배열 → List<Map<String, Object>>
		List<Map<String, Object>> products = objectMapper.readValue(
			responseBody,
			new TypeReference<>() {
			}
		);

		// then: 개수 일치
		assertThat(products.size()).isEqualTo(beforeSize + 1);

		// then: 저장한 상품이 포함되어 있는지 확인
		boolean found = products.stream().anyMatch(p ->
			p.get("name").equals(savedProduct.getName()) &&
				((Integer)p.get("price")).equals(savedProduct.getPrice())
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

	// @DisplayName("deleteProduct: 상품 삭제에 성공한다")
	// @Test
	// public void deleteProduct() throws Exception {
	//
	// 	// given
	// 	final String url = "/products/{productId}";
	// 	Product savedProduct = productSetUp();
	// 	int beforeCount = productRepository.findAll().size();
	//
	// 	// when
	// 	mockMvc.perform(delete(url, savedProduct.getProductId()))
	// 		.andExpect(status().isOk());
	//
	// 	// then
	// 	boolean exists = productRepository.existsById(savedProduct.getProductId());
	// 	assertThat(exists).isFalse();
	// 	assertThat(productRepository.findAll().size()).isEqualTo(beforeCount - 1);
	// }

	@DisplayName("updateProduct: 상품 정보 수정에 성공한다")
	@Test
	public void updateProduct() throws Exception {
		// given
		final String url = "/products/{productId}";
		Product savedProduct = productSetUp();

		final String newName = "상품 정보 수정용 테스트";
		final Integer newPrice = 100;
		final List<String> newImageUrls = new ArrayList<>();
		newImageUrls.add("https://s3.amazonaws.com/.../img1111.jpg");
		newImageUrls.add("https://s3.amazonaws.com/.../img2222.jpg");

		UpdateProductRequest request = new UpdateProductRequest(
			newName, newPrice, 0, 0, "", 0, 0, 0, 5, newImageUrls);

		// when
		ResultActions result = mockMvc.perform(put(url, savedProduct.getProductId())
			.contentType(APPLICATION_JSON_VALUE)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result.andExpect(status().isOk());

		Product product = productRepository.findById(savedProduct.getProductId()).get();

		assertThat(product.getName()).isEqualTo(newName);
		assertThat(product.getPrice()).isEqualTo(newPrice);
	}
}
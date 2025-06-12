package com.travelservice.domain.product.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelservice.domain.product.dto.AddProductRequest;
import com.travelservice.domain.product.dto.UpdateProductRequest;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.repository.ProductRepository;

@Sql("classpath:/db/data.sql")
@SpringBootTest
@AutoConfigureMockMvc  // MockMvc 생성 및 자동 구성
@Transactional
class ProductControllerTest {

	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected ObjectMapper objectMapper; // 직렬화, 역 직렬화를 위한 클래스
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private ProductRepository productRepository;

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
		final Integer totalQuantity = 0;
		final Integer stockQuantity = 0;
		final String description = "";
		final Integer saleStatus = 0;
		final Integer type = 0;
		final Integer duration = 1;

		Product savedProduct = productRepository.save(Product.builder()
			.name(name)
			.price(price)
			.totalQuantity(totalQuantity)
			.stockQuantity(stockQuantity)
			.description(description)
			.saleStatus(saleStatus)
			.type(type)
			.duration(duration)
			.build());

		return savedProduct;
	}

	// @AfterEach
	// public void cleanUp() {
	// 	productRepository.deleteAll();
	// }

	@DisplayName("createProduct: 상품 추가에 성공한다")
	@Test
	public void createProduct() throws Exception {
		// given
		final String url = "/products";
		final String name = "상품 추가용 테스트";
		final Integer price = 10000;
		final Integer totalQuantity = 100;
		final Integer stockQuantity = 99;
		final String description = "상품 추가 테스트입니다";
		final Integer saleStatus = 1;
		final Integer type = 1;
		final Integer duration = 10;
		final AddProductRequest userRequest = new AddProductRequest(name, price, totalQuantity, stockQuantity,
			description, saleStatus, type, duration);

		// 객체 JSON으로 직렬화
		final String requestBody = objectMapper.writeValueAsString(userRequest);

		// when
		// 설정한 내용을 바탕으로 요청 전송
		ResultActions result = mockMvc.perform(post(url)
			.contentType(APPLICATION_JSON_VALUE)
			.content(requestBody));

		// then
		result.andExpect(status().isCreated());

		List<Product> products = productRepository.findAll();

		assertThat(products.size()).isEqualTo(1);
		assertThat(products.get(0).getName()).isEqualTo(name);
		assertThat(products.get(0).getDescription()).isEqualTo(description);
	}

	@DisplayName("getAllProducts: 상품 목록 조회에 성공한다")
	@Test
	public void getAllProducts() throws Exception {
		// given
		final String url = "/products";
		Product savedProduct = productSetUp();

		// when
		final ResultActions resultActions = mockMvc.perform(get(url)
			.accept(APPLICATION_JSON_VALUE));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].name").value(savedProduct.getName()))
			.andExpect(jsonPath("$[0].price").value(savedProduct.getPrice()));
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
			.andExpect(jsonPath("$.description").value(savedProduct.getDescription()));
	}

	@DisplayName("deleteProduct: 상품 삭제에 성공한다")
	@Test
	public void deleteProduct() throws Exception {

		// given
		final String url = "/products/{productId}";
		Product savedProduct = productSetUp();

		// when
		mockMvc.perform(delete(url, savedProduct.getProductId()))
			.andExpect(status().isOk());

		// then
		List<Product> products = productRepository.findAll();

		assertThat(products).isEmpty();
	}

	@DisplayName("updateProduct: 상품 정보 수정에 성공한다")
	@Test
	public void updateProduct() throws Exception {
		// given
		final String url = "/products/{productId}";
		Product savedProduct = productSetUp();

		final String newName = "상품 정보 수정용 테스트";
		final Integer newPrice = 100;

		UpdateProductRequest request = new UpdateProductRequest(newName, newPrice, 0, 0, "", 0, 0, 0);

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
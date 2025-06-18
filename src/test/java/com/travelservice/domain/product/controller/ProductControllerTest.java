package com.travelservice.domain.product.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.entity.ProductDescriptionGroup;
import com.travelservice.domain.product.entity.ProductDescriptionItem;
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
		group1.addItem(ProductDescriptionItem.builder().content("대한항공 직항").sortOrder(1).build());
		group1.addItem(ProductDescriptionItem.builder().content("시그니엘 숙박").sortOrder(2).build());
		ProductDescriptionGroup group2 = ProductDescriptionGroup.builder()
			.title("미포함 사항")
			.type(1)
			.sortOrder(2)
			.build();
		group2.addItem(ProductDescriptionItem.builder().content("식비").sortOrder(1).build());

		savedProduct.addDescriptionGroup(group1);
		savedProduct.addDescriptionGroup(group2);

		productRepository.save(savedProduct);

		return savedProduct;
	}

	// @DisplayName("createProduct: 상품 추가에 성공한다")
	// @Test
	// public void createProduct() throws Exception {
	// 	// given
	// 	final String url = "/products";
	//
	// 	AddProductRequest request = AddProductRequest.builder()
	// 		.name("테스트 상품 1")
	// 		.price(1000)
	// 		.totalQuantity(100)
	// 		.stockQuantity(100)
	// 		.description("상품 설명입니다")
	// 		.saleStatus(0)
	// 		.type(1)
	// 		.duration(5)
	// 		.regionId(5L)
	// 		.imageUrls(List.of(
	// 			"https://s3.amazonaws.com/.../img1.jpg",
	// 			"https://s3.amazonaws.com/.../img2.jpg"
	// 		))
	// 		.descriptionGroups(List.of(
	// 			new ProductDescriptionGroupRequest("포함 사항", 0, 1, List.of(
	// 				ProductDescriptionItemRequest.builder().content("대한항공 직항").sortOrder(1).build(),
	// 				ProductDescriptionItemRequest.builder().content("호텔 숙박").sortOrder(2).build()
	// 			)),
	// 			new ProductDescriptionGroupRequest("미포함 사항", 1, 2, List.of(
	// 				ProductDescriptionItemRequest.builder().content("식비").sortOrder(1).build()
	// 			))
	// 		))
	// 		.build();
	//
	// 	// 객체 JSON으로 직렬화
	// 	final String requestBody = objectMapper.writeValueAsString(request);
	// 	int beforeSize = productRepository.findAll().size();
	//
	// 	// when
	//
	// 	MvcResult result = mockMvc.perform(post(url)
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(requestBody))
	// 		.andExpect(status().isCreated())
	// 		.andReturn();
	//
	// 	String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
	// 	JsonNode root = objectMapper.readTree(response);
	//
	// 	// 공통 응답 검증
	// 	assertThat(root.get("success").asBoolean()).isTrue();
	// 	JsonNode data = root.get("data");
	// 	assertThat(data.get("name").asText()).isEqualTo(request.getName());
	// 	assertThat(data.get("descriptionGroups").size()).isEqualTo(2);
	// 	assertThat(data.get("descriptionGroups").get(0).get("title").asText()).isEqualTo("포함 사항");
	// }
	//
	// @DisplayName("getAllProducts: 상품 목록 조회에 성공한다")
	// @Test
	// public void getAllProducts() throws Exception {
	// 	// given
	// 	final String url = "/products";
	// 	Product savedProduct = productSetUp();
	// 	int expectedSize = productRepository.findAll().size();
	//
	// 	// when
	// 	final ResultActions resultActions = mockMvc.perform(get(url)
	// 		.accept(APPLICATION_JSON_VALUE));
	//
	// 	// then: HTTP 상태
	// 	resultActions.andExpect(status().isOk());
	//
	// 	// then: 응답 본문 파싱
	// 	String responseBody = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
	// 	System.out.println("Response JSON:\n" + responseBody);
	//
	// 	// JSON 배열 → DTO
	// 	ApiResponse<List<ProductListResponse>> response = objectMapper.readValue(
	// 		responseBody,
	// 		new TypeReference<ApiResponse<List<ProductListResponse>>>() {
	// 		}
	// 	);
	//
	// 	// then: 성공 여부 확인
	// 	assertThat(response.isSuccess()).isTrue();
	//
	// 	// then: 개수 일치
	// 	List<ProductListResponse> products = response.getData();
	// 	assertThat(products).hasSize(expectedSize);
	//
	// 	// then: 저장한 상품이 포함되어 있는지 확인
	// 	boolean found = products.stream().anyMatch(p ->
	// 		p.getName().equals(savedProduct.getName()) &&
	// 			p.getPrice().equals(savedProduct.getPrice())
	// 	);
	//
	// 	assertThat(found).isTrue();
	// }
	//
	// @DisplayName("getProductByID: 상품 상세 조회에 성공한다")
	// @Test
	// public void getProductById() throws Exception {
	// 	// given
	// 	Product savedProduct = productSetUp();
	// 	Long productId = savedProduct.getProductId();
	//
	// 	// when
	// 	MvcResult result = mockMvc.perform(get("/products/{productId}", productId)
	// 			.accept(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andReturn();
	//
	// 	// then
	// 	String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
	// 	JsonNode root = objectMapper.readTree(json);
	//
	// 	assertThat(root.get("success").asBoolean()).isTrue();
	//
	// 	JsonNode dataNode = root.get("data");
	// 	assertThat(dataNode.get("productId").asLong()).isEqualTo(productId);
	// 	assertThat(dataNode.get("name").asText()).isEqualTo(savedProduct.getName());
	//
	// 	JsonNode descriptionGroups = dataNode.get("descriptionGroups");
	// 	assertThat(descriptionGroups).isNotNull();
	// 	assertThat(descriptionGroups.size()).isGreaterThan(0);
	//
	// 	JsonNode firstGroup = descriptionGroups.get(0);
	// 	assertThat(firstGroup.get("title").asText()).isEqualTo("포함 사항");
	// 	JsonNode items = firstGroup.get("items");
	// 	assertThat(items.get(0).get("content").asText()).isEqualTo("대한항공 직항");
	// }
	//
	// @DisplayName("deleteProduct: 상품 삭제에 성공한다")
	// @Test
	// public void deleteProduct() throws Exception {
	//
	// 	// given
	// 	String url = "/products/{productId}";
	// 	Product product = productSetUp();
	// 	Long productId = product.getProductId();
	//
	// 	// when
	// 	ResultActions resultActions = mockMvc.perform(delete(url, productId));
	//
	// 	// then
	// 	resultActions
	// 		.andExpect(status().isOk()) // ✅ 204 → 200 OK
	// 		.andExpect(jsonPath("$.success").value(true))
	// 		.andExpect(jsonPath("$.data").doesNotExist()); // 또는 isEmpty(true)
	//
	// 	// DB에 존재하지 않는지 확인
	// 	Optional<Product> deleted = productRepository.findById(productId);
	// 	assertThat(deleted).isEmpty();
	// }
	//
	// @DisplayName("updateProduct: 상품 정보 수정에 성공한다")
	// @Test
	// public void updateProduct() throws Exception {
	// 	// given
	// 	final String url = "/products/{productId}";
	// 	Product savedProduct = productSetUp();
	//
	// 	final String newName = "상품 정보 수정용 테스트";
	// 	final Integer newPrice = 100;
	// 	final List<String> newImageUrls = List.of(
	// 		"https://s3.amazonaws.com/.../newImg1.jpg",
	// 		"https://s3.amazonaws.com/.../newImg2.jpg"
	// 	);
	// 	List<ProductDescriptionGroupRequest> newDescriptionGroups = List.of(
	// 		new ProductDescriptionGroupRequest("포함 사항", 0, 1, List.of(
	// 			ProductDescriptionItemRequest.builder().content("대한항공 직항").sortOrder(1).build(),
	// 			ProductDescriptionItemRequest.builder().content("호텔 숙박").sortOrder(2).build()
	// 		)),
	// 		new ProductDescriptionGroupRequest("미포함 사항", 1, 2, List.of(
	// 			ProductDescriptionItemRequest.builder().content("식비").sortOrder(1).build()
	// 		))
	// 	);
	//
	// 	UpdateProductRequest request = UpdateProductRequest.builder()
	// 		.name(newName)
	// 		.price(savedProduct.getPrice())
	// 		.totalQuantity(savedProduct.getTotalQuantity())
	// 		.stockQuantity(savedProduct.getStockQuantity())
	// 		.description(savedProduct.getDescription())
	// 		.saleStatus(savedProduct.getSaleStatus())
	// 		.type(savedProduct.getType())
	// 		.duration(savedProduct.getDuration())
	// 		.regionId(savedProduct.getRegion().getRegionId())
	// 		.imageUrls(newImageUrls)
	// 		.descriptionGroups(newDescriptionGroups)
	// 		.build();
	//
	// 	// when
	// 	MvcResult result = mockMvc.perform(put(url, savedProduct.getProductId())
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(request)))
	// 		.andExpect(status().isOk())
	// 		.andReturn();
	//
	// 	// then
	// 	String response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
	// 	JsonNode root = objectMapper.readTree(response);
	// 	JsonNode data = root.get("data");
	//
	// 	assertThat(root.get("success").asBoolean()).isTrue();
	// 	assertThat(data.get("name").asText()).isEqualTo(newName);
	// 	assertThat(data.get("descriptionGroups").get(0).get("title").asText()).isEqualTo("포함 사항");
	// 	assertThat(data.get("descriptionGroups").get(0).get("items").get(0).get("content").asText()).isEqualTo(
	// 		"대한항공 직항");
	// }
}

package com.travelservice.domain.admin.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.travelservice.domain.admin.dto.product.AdminProductResponseDto;

@SpringBootTest
@ActiveProfiles("test")
class AdminProductServiceTest {

	@Autowired
	private AdminProductService adminProductService; // 인터페이스 주입 (Impl 아님)

	@Test
	@DisplayName("관리자 상품 목록 페이징 조회 성공 (예시 데이터 기반)")
	void getAdminProductList_withTestData() {
		// given
		int page = 1; // 1부터 시작
		int size = 10;
		Long regionId = 1L; // 시드니

		// when
		AdminProductService.PagedAdminProductListResponse response = adminProductService.getAdminProductList(
			PageRequest.of(page - 1, size), regionId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).isNotEmpty();
		assertThat(response.getTotalElements()).isEqualTo(2);
		assertThat(response.getTotalPages()).isEqualTo(1);
		assertThat(response.getCurrentPage()).isEqualTo(1);

		AdminProductResponseDto first = response.getContent().get(0);
		assertThat(first.getName()).contains("시드니");
		assertThat(first.getRegion().getName()).isEqualTo("시드니");
	}
}

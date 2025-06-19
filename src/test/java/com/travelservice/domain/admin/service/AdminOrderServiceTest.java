package com.travelservice.domain.admin.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.order.AdminOrderDetailDto;
import com.travelservice.domain.admin.dto.order.AdminOrderResponseDto;
import com.travelservice.domain.admin.dto.order.PagedAdminOrderResponseDto;
import com.travelservice.domain.admin.repository.AdminOrderRepository;
import com.travelservice.domain.admin.repository.AdminUserRepository;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.payment.entity.Payment;
import com.travelservice.domain.payment.respository.PaymentRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.enums.OrderStatus;
import com.travelservice.enums.PaymentStatus;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminOrderServiceTest {

	@Autowired
	private AdminOrderService adminOrderService;

	@Autowired
	private AdminUserRepository adminUserRepository;
	@Autowired
	private AdminOrderRepository adminOrderRepository;
	@Autowired
	private PaymentRepository paymentRepository;

	private User testUser;
	private Order testOrder;
	private Payment testPayment;

	@BeforeEach
	void setUp() {
		// 1. 테스트 유저 생성
		testUser = adminUserRepository.save(User.builder()
			.email("test%d@example.com".formatted(System.currentTimeMillis()))
			.name("테스터")
			.password("password")
			.phoneNumber("010-1234-5678")
			.build());

		// 2. 주문 데이터 생성
		testOrder = adminOrderRepository.save(Order.builder()
			.user(testUser)
			.status(OrderStatus.PENDING)
			.totalQuantity(2)
			.orderDate(LocalDateTime.now())
			.build());

		// 3. 결제 데이터 생성 및 저장
		testPayment = paymentRepository.save(Payment.builder()
			.order(this.testOrder)
			.status(PaymentStatus.PAID)
			.method("CARD")
			.cardNumber("1234-5678-9000-1234")
			.paidAt(LocalDateTime.now())
			.build());
	}

	@Test
	void getOrderList_basic() {
		// given
		int page = 1;
		int size = 10;
		String status = null;
		String startDate = null;
		String endDate = null;

		// when
		PagedAdminOrderResponseDto response = adminOrderService.findOrders(page, size, status, startDate, endDate);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).isNotEmpty();

		assertThat(response.getTotalElements()).isGreaterThan(0);
		assertThat(response.getTotalPages()).isGreaterThanOrEqualTo(1);

		AdminOrderResponseDto order = response.getContent().get(0);
		assertThat(order.getOrderId()).isNotNull();
		assertThat(order.getUserName()).isNotNull();
	}

	@Test
	void getOrderDetail_withPayment() {
		// given
		Long orderId = testOrder.getOrderId();

		// when
		AdminOrderDetailDto detail = adminOrderService.getOrderDetail(orderId);

		// then
		assertThat(detail).isNotNull();
		assertThat(detail.getOrderId()).isEqualTo(orderId);
		assertThat(detail.getPayment()).isNotNull();
		assertThat(detail.getPayment().getStatus()).isEqualTo(PaymentStatus.PAID.name());
		assertThat(detail.getPayment().getCardNumber()).endsWith("1234");
	}
}

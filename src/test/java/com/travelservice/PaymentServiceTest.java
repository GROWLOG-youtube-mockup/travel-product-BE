package com.travelservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.repository.OrderRepository;
import com.travelservice.domain.payment.dto.PaymentApproveRequestDto;
import com.travelservice.domain.payment.entity.Payment;
import com.travelservice.domain.payment.respository.PaymentRepository;
import com.travelservice.domain.payment.service.PaymentService;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.enums.OrderStatus;
import com.travelservice.enums.PaymentStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PaymentServiceTest {

	@InjectMocks
	private PaymentService paymentService;

	@Mock
	private PaymentRepository paymentRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private RedisTemplate<String, String> redisTemplate;
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private UserRepository userRepository;

	private PaymentApproveRequestDto validRequest;

	@BeforeEach
	void setUp() throws Exception {
		validRequest = PaymentApproveRequestDto.builder()
			.paymentKey("toss-generated-key-123")
			.orderId("1")
			.amount(10000)
			.build();

		// tossSecretKey 리플렉션으로 주입
		Field field = PaymentService.class.getDeclaredField("tossSecretKey");
		field.setAccessible(true);
		field.set(paymentService, "test_sk_dummy_secret");
	}

	@Test
	void approve_success() {
		when(redisTemplate.hasKey("1")).thenReturn(true);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(Order.builder()
			.orderId(1L)
			.orderDate(LocalDateTime.now())
			.status(OrderStatus.PENDING)
			.totalQuantity(1)
			.user(User.builder().userId(1L).build())
			.build()));

		Payment dummyPayment = Payment.builder()
			.paymentId(1L)
			.status(PaymentStatus.PAID)
			.method("카드")
			.paidAt(LocalDateTime.now())
			.build();
		when(paymentRepository.save(any(Payment.class))).thenReturn(dummyPayment);

		assertDoesNotThrow(() -> paymentService.approve(validRequest));
	}

	@Test
	void approve_fail_redis_missing() {
		PaymentApproveRequestDto dto = new PaymentApproveRequestDto("payKey", "1", 50000, "toss", "tx123");
		when(redisTemplate.hasKey("1")).thenReturn(false);

		assertThrows(IllegalArgumentException.class, () -> paymentService.approve(dto));
	}

	@Test
	void approve_fail_order_not_found() {
		PaymentApproveRequestDto dto = new PaymentApproveRequestDto("payKey", "999", 10000, "toss", "tx123");
		when(redisTemplate.hasKey("999")).thenReturn(true);
		when(orderRepository.findById(999L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.approve(dto));
		System.out.println("실제 예외 메시지: " + exception.getMessage());
	}

	@Test
	void approve_fail_toss_error() {
		when(redisTemplate.hasKey("wrong_order_id")).thenReturn(true);
		when(orderRepository.findById(anyLong()))
			.thenReturn(Optional.of(Order.builder().orderId(999L).build()));

		// Toss 응답 오류 시나리오 mocking
		HttpClientErrorException errorResponse =
			HttpClientErrorException.create(
				HttpStatus.UNAUTHORIZED,
				"Unauthorized",
				HttpHeaders.EMPTY,
				"{\"message\":\"Invalid paymentKey\"}".getBytes(StandardCharsets.UTF_8),
				StandardCharsets.UTF_8
			);

		when(restTemplate.postForEntity(anyString(), any(), eq(JsonNode.class)))
			.thenThrow(errorResponse);

		PaymentApproveRequestDto invalidRequest = PaymentApproveRequestDto.builder()
			.paymentKey("wrong_key")
			.orderId("wrong_order_id")
			.amount(9999)
			.build();

		Exception exception = assertThrows(RuntimeException.class, () -> {
			paymentService.approve(invalidRequest);
		});

		assertTrue(exception.getMessage().contains("승인 실패"));
	}
}

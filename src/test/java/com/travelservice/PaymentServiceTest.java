package com.travelservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.repository.OrderRepository;
import com.travelservice.domain.payment.dto.PaymentApproveRequestDto;
import com.travelservice.domain.payment.dto.PaymentResponseDto;
import com.travelservice.domain.payment.entity.Payment;
import com.travelservice.domain.payment.respository.PaymentRepository;
import com.travelservice.domain.payment.service.PaymentService;
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

	@Test
	void approve_success() {
		try {
			// given
			PaymentApproveRequestDto dto = new PaymentApproveRequestDto("payKey", "1", 50000);

			Order mockOrder = Order.builder()
				.orderId(1L)
				.status(OrderStatus.PENDING)
				.orderDate(LocalDateTime.now())
				.totalQuantity(1)
				.build();

			JsonNode jsonNode = new ObjectMapper().readTree("""
                {
                    "method": "카드",
                    "card": { "number": "1234-****" }
                }
            """);

			when(redisTemplate.hasKey("1")).thenReturn(true);
			when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
			when(restTemplate.postForEntity(anyString(), any(), eq(JsonNode.class)))
				.thenReturn(ResponseEntity.ok(jsonNode));
			when(paymentRepository.save(any())).thenAnswer(invocation -> {
				Payment payment = invocation.getArgument(0);
				payment.setPaymentId(999L);
				return payment;
			});

			// when
			PaymentResponseDto result = paymentService.approve(dto);

			// then
			assertEquals(PaymentStatus.PAID.name(), result.getStatus());
			assertEquals("카드", result.getMethod());
			assertNotNull(result.getPaidAt());
			verify(redisTemplate).delete("1");

		} catch (Exception e) {
			throw new RuntimeException("approve_success 테스트 중 오류 발생", e);
		}
	}

	@Test
	void approve_fail_redis_missing() {
		// given
		PaymentApproveRequestDto dto = new PaymentApproveRequestDto("payKey", "1", 50000);
		when(redisTemplate.hasKey("1")).thenReturn(false);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> paymentService.approve(dto));
	}

	@Test
	void approve_fail_toss_error() {
		try {
			// given
			PaymentApproveRequestDto dto = new PaymentApproveRequestDto("payKey", "1", 50000);
			Order mockOrder = Order.builder()
				.orderId(1L)
				.status(OrderStatus.PENDING)
				.build();

			String errorJson = """
                {
                    "code": "NOT_FOUND_PAYMENT_SESSION",
                    "message": "결제 세션 없음"
                }
            """;

			when(redisTemplate.hasKey("1")).thenReturn(true);
			when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

			HttpClientErrorException exception = HttpClientErrorException.create(
				HttpStatus.BAD_REQUEST,
				"400",
				HttpHeaders.EMPTY,
				errorJson.getBytes(StandardCharsets.UTF_8),
				StandardCharsets.UTF_8
			);

			when(restTemplate.postForEntity(anyString(), any(), eq(JsonNode.class)))
				.thenThrow(exception);

			// when & then
			RuntimeException ex = assertThrows(
				RuntimeException.class,
				() -> paymentService.approve(dto)
			);
			assertTrue(ex.getMessage().contains("결제 승인 실패"));
		} catch (Exception e) {
			throw new RuntimeException("approve_fail_toss_error 테스트 중 오류 발생", e);
		}
	}

	@Test
	void approve_fail_order_not_found() {
		// given
		PaymentApproveRequestDto dto = new PaymentApproveRequestDto("payKey", "999", 10000);
		when(redisTemplate.hasKey("999")).thenReturn(true);
		when(orderRepository.findById(999L)).thenReturn(Optional.empty());

		// when & then
		RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.approve(dto));
		System.out.println("실제 예외 메시지: " + exception.getMessage());
	}
}

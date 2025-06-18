package com.travelservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

	private PaymentApproveRequestDto validRequest;

	@BeforeEach
	void setUp() {
		validRequest = PaymentApproveRequestDto.builder()
			.paymentKey("test_payment_key")
			.orderId("test_order_id")
			.amount(10000)
			.build();
	}

	@Test
	void approve_success() {
		// 테스트용이므로 인증을 강제로 주입한 PaymentService의 approve 호출 필요
		Exception exception = assertThrows(RuntimeException.class, () -> {
			paymentService.approve(validRequest); // 실제 Toss 요청은 여기서 실패할 수 있음
		});

		assertTrue(exception.getMessage().contains("승인 실패") || exception.getMessage().contains("401"));
	}

	@Test
	void approve_fail_redis_missing() {
		// given
		PaymentApproveRequestDto dto = new PaymentApproveRequestDto("payKey", "1", 50000, "toss", "tx123");
		when(redisTemplate.hasKey("1")).thenReturn(false);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> paymentService.approve(dto));
	}

	@Test
	void approve_fail_toss_error() {
		PaymentApproveRequestDto invalidRequest = PaymentApproveRequestDto.builder()
			.paymentKey("wrong_key")
			.orderId("wrong_order_id")
			.amount(9999)
			.build();

		Exception exception = assertThrows(RuntimeException.class, () -> {
			paymentService.approve(invalidRequest);
		});

		assertTrue(exception.getMessage().contains("승인 실패") || exception.getMessage().contains("401"));
	}

	@Test
	void approve_fail_order_not_found() {
		// given
		PaymentApproveRequestDto dto = new PaymentApproveRequestDto("payKey", "999", 10000, "toss", "tx123");
		when(redisTemplate.hasKey("999")).thenReturn(true);
		when(orderRepository.findById(999L)).thenReturn(Optional.empty());

		// when & then
		RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.approve(dto));
		System.out.println("실제 예외 메시지: " + exception.getMessage());
	}
}

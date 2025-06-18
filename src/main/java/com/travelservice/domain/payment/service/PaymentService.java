package com.travelservice.domain.payment.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
import com.travelservice.enums.OrderStatus;
import com.travelservice.enums.PaymentStatus;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final RestTemplate restTemplate;

	@Value("${toss.secret-key}")
	private String tossSecretKey;

	@Transactional
	public PaymentResponseDto approve(PaymentApproveRequestDto requestDto) throws IOException {
		// Redis에 저장된 결제 요청 정보 확인
		if (requestDto.getOrderId() == null || !Boolean.TRUE.equals(redisTemplate.hasKey(requestDto.getOrderId()))) {
			throw new IllegalArgumentException("유효하지 않은 주문 ID입니다.");
		}

		// Toss API 호출
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(tossSecretKey, ""); // Base64 인코딩 자동 처리
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = new HashMap<>();
		body.put("paymentKey", requestDto.getPaymentKey()); //Toss에서 받은 값을 넣어야함
		body.put("orderId", requestDto.getOrderId());
		body.put("amount", requestDto.getAmount());

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<JsonNode> response;
		try {
			response = restTemplate.postForEntity("https://api.tosspayments.com/v1/payments/confirm", entity, JsonNode.class);
		} catch (HttpClientErrorException e) {
			JsonNode errorBody = new ObjectMapper().readTree(e.getResponseBodyAsString());
			throw new RuntimeException("결제 승인 실패: " + errorBody.get("message").asText());
		}

		JsonNode data = response.getBody();

		System.out.println("🧾 Toss 응답 전체: " + data.toPrettyString());

		if (data == null || !data.has("method")) {
			System.out.println("❗ 결제 응답에 'method'가 없습니다. data = " + data);
			throw new IllegalStateException("결제 승인 응답에 필수 필드 누락");
		}

		String method = data.has("method") ? data.get("method").asText() : "";
		String cardNumber = "";
		String accountNumber = "";
		String bank = "";
		String mobilePhone = "";

		switch (method) {
			case "카드" -> {
				JsonNode card = data.get("card");
				if (card != null && card.has("number")) {
					cardNumber = card.get("number").asText();
				}
			}
			case "가상계좌" -> {
				JsonNode virtual = data.get("virtualAccount");
				if (virtual != null && virtual.has("accountNumber")) {
					accountNumber = virtual.get("accountNumber").asText();
				}
			}
			case "계좌이체" -> {
				JsonNode transfer = data.get("transfer");
				if (transfer != null && transfer.has("bank")) {
					bank = transfer.get("bank").asText();
				}
			}
			case "휴대폰" -> {
				JsonNode phone = data.get("mobilePhone");
				if (phone != null && phone.has("customerMobilePhone")) {
					mobilePhone = phone.get("customerMobilePhone").asText();
				}
			}
			default -> System.out.println("⚠️ 예상치 못한 결제수단: " + method);
		}

		Order order = orderRepository.findById(Long.valueOf(requestDto.getOrderId()))
				.orElseThrow(() -> new RuntimeException("유효하지 않은 주문 ID입니다."));

		Payment payment = Payment.builder()
				.order(order)
				.paymentKey(requestDto.getPaymentKey())
				.method(method)
				.cardNumber(cardNumber)
				.accountNumber(accountNumber)
				.bank(bank)
				.mobilePhone(mobilePhone)
				.status(PaymentStatus.PAID)
				.paidAt(LocalDateTime.now())
				.build();

		Payment saved = paymentRepository.save(payment);
		order.setStatus(OrderStatus.PAID);
		redisTemplate.delete(requestDto.getOrderId());

		return PaymentResponseDto.builder()
				.paymentId(saved.getPaymentId())
				.status(saved.getStatus().name())
				.method(saved.getMethod())
				.paidAt(saved.getPaidAt().toString())
				.build();
	}

	public PaymentResponseDto getPaymentStatusByOrderId(Long orderId) {
		Payment payment = paymentRepository.findByOrder_OrderId(orderId)
			.orElseThrow(() -> new RuntimeException("해당 주문에 대한 결제 정보가 없습니다."));

		return PaymentResponseDto.builder()
			.paymentId(payment.getPaymentId())
			.status(payment.getStatus().name())
			.method(payment.getMethod())
			.paidAt(payment.getPaidAt().toString())
			.build();
	}

	@PostConstruct
	public void init() {
		try {
			redisTemplate.opsForValue().set("order_20240618_0001", "dummy");
		} catch (Exception e) {
			System.out.println("⚠️ Redis 연결 실패: " + e.getMessage());
		}
	}
}

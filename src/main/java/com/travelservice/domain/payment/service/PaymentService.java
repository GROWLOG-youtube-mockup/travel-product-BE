package com.travelservice.domain.payment.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelservice.domain.cart.entity.Cart;
import com.travelservice.domain.cart.repository.CartRepository;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.entity.OrderItem;
import com.travelservice.domain.order.repository.OrderItemRepository;
import com.travelservice.domain.order.repository.OrderRepository;
import com.travelservice.domain.payment.dto.PaymentApproveRequestDto;
import com.travelservice.domain.payment.dto.PaymentResponseDto;
import com.travelservice.domain.payment.entity.Payment;
import com.travelservice.domain.payment.respository.PaymentRepository;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.enums.OrderStatus;
import com.travelservice.enums.PaymentStatus;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final CartRepository cartItemRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;

	private final RedisTemplate<String, String> redisTemplate;
	private final RestTemplate restTemplate;
	private final String tossSecretKey;
	private final PasswordEncoder passwordEncoder;

	//생성자에서 .env 파일 로드 및 tossSecretKey 초기화
	public PaymentService(PaymentRepository paymentRepository,
		OrderRepository orderRepository,
		RedisTemplate<String, String> redisTemplate,
		RestTemplate restTemplate,
		UserRepository userRepository,
		CartRepository cartItemRepo,
		OrderItemRepository orderItemRepo,
		ProductRepository productRepo,
		PasswordEncoder passwordEncoder) {

		this.paymentRepository = paymentRepository;
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.cartItemRepository = cartItemRepo;
		this.orderItemRepository = orderItemRepo;
		this.productRepository = productRepo;
		this.redisTemplate = redisTemplate;
		this.restTemplate = restTemplate;
		this.passwordEncoder = passwordEncoder;

		Dotenv dotenv = Dotenv.load(); //.env 파일 로드 (루트 경로에 위치해야 함)
		this.tossSecretKey = dotenv.get("TOSS_SECRET_KEY"); //키 가져오기
	}

	// @Value("${toss.secret-key}")
	// private String tossSecretKey;

	@Transactional
	public PaymentResponseDto approve(PaymentApproveRequestDto requestDto) throws IOException {
		String orderIdKey = String.valueOf(requestDto.getOrderId());

		// Redis에 저장된 결제 요청 정보 확인
		if (requestDto.getOrderId() == null || !Boolean.TRUE.equals(redisTemplate.hasKey(orderIdKey))) {
			throw new CustomException(ErrorCode.INVALID_ORDER_ID);
		}

		//테스트용 우회 로직 (paymentKey가 가짜일 경우)
		if (requestDto.getPaymentKey().startsWith("toss-generated-key")) {
			// Mock data
			String method = "카드";

			Order order = orderRepository.findById(requestDto.getOrderId())
				.orElseThrow(() -> new CustomException(ErrorCode.INVALID_ORDER_ID));

			int expectedAmount = order.getOrderItems().stream()
				.mapToInt(i -> i.getProduct().getPrice() * i.getPeopleCount())
				.sum();

			if (expectedAmount != requestDto.getAmount()) {
				throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
			}

			Payment payment = Payment.builder()
				.order(order)
				.paymentKey(requestDto.getPaymentKey())
				.method(method)
				.cardNumber("1234-****-****-5678")
				.status(PaymentStatus.PAID)
				.paidAt(LocalDateTime.now())
				.build();

			Payment saved = paymentRepository.save(payment);
			order.setStatus(OrderStatus.PAID);
			redisTemplate.delete(orderIdKey);

			return PaymentResponseDto.builder()
				.paymentId(saved.getPaymentId())
				.status(saved.getStatus().name())
				.method(saved.getMethod())
				.paidAt(saved.getPaidAt().toString())
				.build();
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
			response = restTemplate.postForEntity("https://api.tosspayments.com/v1/payments/confirm", entity,
				JsonNode.class);
		} catch (HttpClientErrorException e) {
			JsonNode errorBody = new ObjectMapper().readTree(e.getResponseBodyAsString());
			throw new RuntimeException("결제 승인 실패: " + errorBody.get("message").asText());
		}

		JsonNode data = response.getBody();

		System.out.println("🧾 Toss 응답 전체: " + data.toPrettyString());

		if (data == null || !data.has("method")) {
			System.out.println("❗ 결제 응답에 'method'가 없습니다. data = " + data);
			throw new CustomException(ErrorCode.PAYMENT_APPROVE_FAILED);
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

		// Toss 응답 처리 후 order 조회
		Order order = orderRepository.findById(requestDto.getOrderId())
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_ORDER_ID));

		int expectedAmount = order.getOrderItems().stream()
			.mapToInt(i -> i.getProduct().getPrice() * i.getPeopleCount())
			.sum();

		if (expectedAmount != requestDto.getAmount()) {
			throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
		}

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
		redisTemplate.delete(orderIdKey);

		return PaymentResponseDto.builder()
			.paymentId(saved.getPaymentId())
			.status(saved.getStatus().name())
			.method(saved.getMethod())
			.paidAt(saved.getPaidAt().toString())
			.build();
	}

	public Order createOrderFromCartItem(User user, Long cartItemId) {
		Cart cartItem = cartItemRepository.findById(cartItemId)
			.orElseThrow(() -> new IllegalArgumentException("해당 장바구니 항목이 없습니다."));

		// 🛡 본인 장바구니 항목인지 확인
		if (!cartItem.getUser().getUserId().equals(user.getUserId())) {
			throw new CustomException(ErrorCode.CART_ITEM_ACCESS_DENIED);
		}

		Product product = cartItem.getProduct();
		int quantity = cartItem.getQuantity();

		// 재고 확인 등 검증
		if (product.getStock() < quantity) {
			throw new RuntimeException("재고가 부족합니다.");
		}

		// 주문 생성
		Order order = Order.builder()
			.user(user)
			.orderDate(LocalDateTime.now())
			.status(OrderStatus.PENDING)
			.totalQuantity(quantity)
			.build();

		Order savedOrder = orderRepository.save(order);

		OrderItem orderItem = OrderItem.builder()
			.order(savedOrder)
			.product(product)
			.price(product.getPrice() * quantity)
			.build();

		orderItemRepository.save(orderItem);

		// 재고 차감 및 장바구니 항목 제거
		product.reduceStock(quantity);
		productRepository.save(product);

		cartItemRepository.delete(cartItem);

		return savedOrder;
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

	public Payment getPaymentStatus(Long orderId, Long userId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		if (!order.getUser().getUserId().equals(userId)) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
		}

		return paymentRepository.findByOrder(order)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
	}

	// @PostConstruct
	// public void init() {
	// 	try {
	// 		// 테스트용 사용자 확인 (user_id = 1)
	// 		Optional<User> optionalUser = userRepository.findById(1L);
	// 		User user = optionalUser.orElseGet(() -> {
	// 			User newUser = User.builder()
	// 				.userId(1L)
	// 				.email("test@example.com")
	// 				.name("Test User")
	// 				.password(passwordEncoder.encode("test1234"))
	// 				.phoneNumber("01012345678")
	// 				.build();
	// 			return userRepository.save(newUser);
	// 		});
	//
	// 		// 테스트용 주문 생성
	// 		Order order = Order.builder()
	// 			.user(user)
	// 			.orderDate(LocalDateTime.now())
	// 			.totalQuantity(1)
	// 			.status(OrderStatus.PENDING)
	// 			.build();
	//
	// 		Order savedOrder = orderRepository.save(order);
	//
	// 		// Redis에 등록
	// 		redisTemplate.opsForValue().set(savedOrder.getOrderId().toString(), "dummy");
	// 		System.out.println("✅ 테스트 주문 ID: " + savedOrder.getOrderId());
	// 	} catch (Exception e) {
	// 		System.out.println("⚠️ Redis 연결 실패: " + e.getMessage());
	// 	}
	// }

	@Transactional
	public void cancel(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		Payment payment = paymentRepository.findByOrder(order)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

		if (payment.getStatus() == PaymentStatus.CANCELLED) {
			throw new CustomException(ErrorCode.ALREADY_CANCELLED);
		}

		//toss 결제 취소 처리
		String paymentKey = payment.getPaymentKey();
		if (paymentKey.startsWith("toss-generated-key")) { //테스트 시
			log.info("테스트 결제 - Toss 취소 API 호출 생략");
		} else {
			String cancelUrl = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

			HttpHeaders headers = new HttpHeaders();
			headers.setBasicAuth(tossSecretKey, ""); // Base64 인코딩 자동 처리
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, Object> body = new HashMap<>();
			body.put("cancelReason", "사용자 요청에 의한 취소"); // 취소 사유 설정

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

			try {
				ResponseEntity<JsonNode> response = restTemplate.postForEntity(cancelUrl, entity, JsonNode.class);
				if (!response.getStatusCode().is2xxSuccessful()) {
					throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);
				}
			} catch (HttpClientErrorException e) {
				log.error("❌ Toss 결제 취소 API 실패: {}", e.getResponseBodyAsString());
				throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);
			}
		}

		// 결제 상태 업데이트
		payment.setStatus(PaymentStatus.CANCELLED);

		// 재고 복원
		if (order.getOrderItems().isEmpty()) {
			throw new CustomException(ErrorCode.ORDER_ITEM_NOT_FOUND); // 주문 항목(orderitem)이 없을 때 예외 처리
		}
		OrderItem item = order.getOrderItems().get(0); // 단건이므로 인덱스 0
		Product product = item.getProduct();
		product.increaseStock(item.getPeopleCount());

		// 주문 상태는 무조건 취소
		order.setStatus(OrderStatus.CANCELLED);
		order.setCancelDate(LocalDateTime.now());
	}

}

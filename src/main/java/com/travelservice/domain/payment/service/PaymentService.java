package com.travelservice.domain.payment.service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import com.travelservice.domain.cart.entity.CartItem;
import com.travelservice.domain.cart.repository.CartItemRepository;
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
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final RestTemplate restTemplate;
	private final UserRepository userRepository;
	private final String tossSecretKey;
	private final CartItemRepository cartItemRepo;
	private final OrderItemRepository orderItemRepo;
	private final ProductRepository productRepo;

	//생성자에서 .env 파일 로드 및 tossSecretKey 초기화
	public PaymentService(PaymentRepository paymentRepository,
		OrderRepository orderRepository,
		RedisTemplate<String, String> redisTemplate,
		RestTemplate restTemplate,
		UserRepository userRepository,
		CartItemRepository cartItemRepo,
		OrderItemRepository orderItemRepo,
		ProductRepository productRepo) {

		this.paymentRepository = paymentRepository;
		this.orderRepository = orderRepository;
		this.redisTemplate = redisTemplate;
		this.restTemplate = restTemplate;
		this.userRepository = userRepository;
		this.cartItemRepo = cartItemRepo;
		this.orderItemRepo = orderItemRepo;
		this.productRepo = productRepo;

		Dotenv dotenv = Dotenv.load(); //.env 파일 로드 (루트 경로에 위치해야 함)
		this.tossSecretKey = dotenv.get("TOSS_SECRET_KEY"); //키 가져오기
	}

	// @Value("${toss.secret-key}")
	// private String tossSecretKey;

	@Transactional
	public PaymentResponseDto approve(PaymentApproveRequestDto requestDto) throws IOException {
		// Redis에 저장된 결제 요청 정보 확인
		if (requestDto.getOrderId() == null || !Boolean.TRUE.equals(redisTemplate.hasKey(requestDto.getOrderId()))) {
			throw new IllegalArgumentException("유효하지 않은 주문 ID입니다.");
		}

		//테스트용 우회 로직 (paymentKey가 가짜일 경우)
		if (requestDto.getPaymentKey().startsWith("toss-generated-key")) {
			// Mock data
			String method = "카드";
			Order order = orderRepository.findById(Long.valueOf(requestDto.getOrderId()))
				.orElseThrow(() -> new RuntimeException("유효하지 않은 주문 ID입니다."));

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
			redisTemplate.delete(requestDto.getOrderId());

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

	public Order createOrderFromCartItem(User user, Long cartItemId) {
		CartItem cartItem = cartItemRepo.findById(cartItemId)
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
			.quantity(quantity)
			.price(product.getPrice() * quantity)
			.build();

		orderItemRepo.save(orderItem);

		// 재고 차감 및 장바구니 항목 제거
		product.reduceStock(quantity);
		productRepo.save(product);

		cartItemRepo.delete(cartItem);

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

	public Payment getPaymentStatus(Long orderId, User user) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문 없음"));

		if (!order.getUser().getUserId().equals(user.getUserId())) {
			throw new CustomException(ErrorCode.ORDER_ACCESS_DENIED);
		}

		return paymentRepository.findByOrder(order)
			.orElseThrow(() -> new IllegalArgumentException("결제 정보 없음"));
	}

	@PostConstruct
	public void init() {
		try {
			// 테스트용 사용자 확인 (user_id = 1)
			Optional<User> optionalUser = userRepository.findById(1L);
			User user = optionalUser.orElseGet(() -> {
				User newUser = User.builder()
					.userId(1L)
					.email("test@example.com")
					.name("Test User")
					.password("test1234")
					.phoneNumber("01012345678")
					.build();
				return userRepository.save(newUser);
			});

			// 테스트용 주문 생성
			Order order = Order.builder()
				.user(user)
				.orderDate(LocalDateTime.now())
				.totalQuantity(1)
				.status(OrderStatus.PENDING)
				.build();

			Order savedOrder = orderRepository.save(order);

			// Redis에 등록
			redisTemplate.opsForValue().set(savedOrder.getOrderId().toString(), "dummy");
			System.out.println("✅ 테스트 주문 ID: " + savedOrder.getOrderId());
		} catch (Exception e) {
			System.out.println("⚠️ Redis 연결 실패: " + e.getMessage());
		}
	}

	@Transactional
	public Payment payNow(Order order) {
		Payment payment = Payment.builder()
			.order(order)
			.paymentKey("test-key")
			.method("카드")
			.status(PaymentStatus.PAID)
			.paidAt(LocalDateTime.now())
			.build();

		order.setStatus(OrderStatus.PAID);

		orderRepository.save(order);
		return paymentRepository.save(payment);
	}

	@Transactional
	public void cancel(Long orderId) {
		Payment payment = paymentRepository.findByOrder_OrderId(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

		if (payment.getStatus() == PaymentStatus.CANCELLED) {
			throw new CustomException(ErrorCode.ALREADY_CANCELLED);
		}

		payment.setStatus(PaymentStatus.CANCELLED);
		payment.getOrder().setStatus(OrderStatus.CANCELLED);
	}

}

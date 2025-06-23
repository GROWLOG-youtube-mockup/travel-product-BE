package com.travelservice.domain.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.cart.entity.CartItem;
import com.travelservice.domain.cart.repository.CartItemRepository;
import com.travelservice.domain.order.dto.OrderItemDto;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.entity.OrderItem;
import com.travelservice.domain.order.repository.OrderRepository;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.enums.OrderStatus;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepo;
	private final ProductRepository productRepo;
	private final UserRepository userRepo;
	private final CartItemRepository cartItemRepo;
	private final RedisTemplate<String, String> redisTemplate;
	/*private final OrderRepository orderRepository;
	private final CartItemRepository cartItemRepository;*/

	@Transactional
	public Order createOrder(String email, List<OrderItemDto> itemDtos) {
		if (itemDtos == null || itemDtos.isEmpty()) {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		User user = userRepo.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Order order = new Order();
		order.setUser(user);
		order.setOrderDate(LocalDateTime.now());

		int totalQty = 0;
		for (OrderItemDto dto : itemDtos) {
			Product product = productRepo.findById(dto.getProductId())
				.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

			if (product.getStockQuantity() < dto.getQuantity()) {
				throw new CustomException(ErrorCode.OUT_OF_STOCK);
			}

			product.setStockQuantity(product.getStockQuantity() - dto.getQuantity());

			OrderItem item = OrderItem.builder()
				.order(order)
				.product(product)
				.peopleCount(dto.getQuantity())
				.startDate(dto.getStartDate())
				.build();

			order.getOrderItems().add(item);
			totalQty += dto.getQuantity();
		}
		order.setTotalQuantity(totalQty);
		Order savedOrder = orderRepo.save(order);
		redisTemplate.opsForValue().set(savedOrder.getOrderId().toString(), "dummy");
		return savedOrder;
	}

	@Transactional
	public Order createOrderFromCart(String email) {
		User user = userRepo.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("유저 없음"));

		List<CartItem> cartItems = cartItemRepo.findByUser(user);
		if (cartItems.isEmpty()) {
			throw new RuntimeException("장바구니 비어있음");
		}

		Order order = new Order();
		order.setUser(user);
		order.setOrderDate(LocalDateTime.now());

		int totalQty = 0;
		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();

			if (product.getStockQuantity() < cartItem.getQuantity()) {
				throw new RuntimeException("재고 부족");
			}

			product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

			OrderItem item = OrderItem.builder()
					.order(order)
					.product(product)
					.peopleCount(cartItem.getQuantity())
					.startDate(cartItem.getStartDate())
					.build();
			orderItems.add(item);
			totalQty += cartItem.getQuantity();
		}

		order.setOrderItems(orderItems);
		order.setTotalQuantity(totalQty);
		Order savedOrder = orderRepo.save(order);

		cartItemRepo.deleteAll(cartItems);

		return savedOrder;
	}

	public Order findById(Long orderId) {
		return orderRepo.findById(orderId)
				.orElseThrow(() -> new RuntimeException("주문 없음"));
	}

	public List<Order> findOrdersByEmail(String email) {
		User user = userRepo.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("유저 없음"));
		return orderRepo.findByUser(user);
	}

	public Order findByIdAndUser(Long id, User user) {
		Order order = orderRepo.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("주문 없음"));
		if (!order.getUser().getUserId().equals(user.getUserId())) {
			throw new CustomException(ErrorCode.INVALID_ACCESSTOKEN);
		}
		return order;
	}

	public List<Order> findByUser(User user) {
		return orderRepo.findByUser(user);
	}

	public Order createOrderFromCartItem(User user, Long cartItemId) {
		// 👉 cartItemId에 해당하는 CartItem 조회
		CartItem cartItem = cartItemRepo.findById(cartItemId)
			.orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

		// 👉 단일 상품 기반으로 OrderItem 만들기
		OrderItem orderItem = OrderItem.builder()
			.product(cartItem.getProduct())
			.quantity(cartItem.getQuantity())
			.price(cartItem.getProduct().getPrice())
			.build();

		// 👉 Order 만들기
		Order order = Order.builder()
			.user(user)
			.orderDate(LocalDateTime.now())
			.status(OrderStatus.PENDING)
			.totalQuantity(cartItem.getQuantity())
			.orderItems(List.of(orderItem))
			.build();

		orderItem.setOrder(order); // 양방향 연관관계 설정

		return orderRepo.save(order);
	}

}

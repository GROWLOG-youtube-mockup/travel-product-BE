package com.travelservice.domain.order.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.cart.entity.Cart;
import com.travelservice.domain.cart.repository.CartRepository;
import com.travelservice.domain.order.dto.OrderItemDto;
import com.travelservice.domain.order.dto.OrderResponseDto;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.entity.OrderItem;
import com.travelservice.domain.order.repository.OrderItemRepository;
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
	private final OrderItemRepository orderItemRepo;
	private final ProductRepository productRepo;
	private final UserRepository userRepo;
	private final CartRepository cartRepo;
	private final RedisTemplate<String, String> redisTemplate;

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

			if (product.getStockQuantity() < dto.getPeopleCount()) {
				throw new CustomException(ErrorCode.OUT_OF_STOCK);
			}

			product.setStockQuantity(product.getStockQuantity() - dto.getPeopleCount());

			OrderItem item = OrderItem.builder()
				.order(order)
				.product(product)
				.peopleCount(dto.getPeopleCount())
				.startDate(dto.getStartDate())
				.price(product.getPrice()) //가격은 현재 가격으로 저장하기 위해 추가
				.build();

			order.getOrderItems().add(item);
			totalQty += dto.getPeopleCount();
		}
		order.setTotalQuantity(totalQty);
		Order savedOrder = orderRepo.save(order);
		redisTemplate.opsForValue().set(savedOrder.getOrderId().toString(), "dummy");
		return savedOrder;
	}

	@Transactional
	public Order createOrderFromCart(String email, Long cartItemId) {

		Cart cartItem = cartRepo.findById(cartItemId)
			.orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

		User user = userRepo.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		if (!cartItem.getUser().getUserId().equals(user.getUserId())) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
		}

		Product product = cartItem.getProduct();
		if (product.getStockQuantity() < cartItem.getQuantity()) {
			throw new CustomException(ErrorCode.OUT_OF_STOCK);
		}
		product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

		Order order = Order.builder()
			.user(cartItem.getUser())
			.orderDate(LocalDateTime.now())
			.totalQuantity(cartItem.getQuantity())
			.status(OrderStatus.PENDING)
			.build();
		orderRepo.save(order);

		OrderItem orderItem = OrderItem.builder()
			.order(order)
			.product(cartItem.getProduct())
			.peopleCount(cartItem.getQuantity())
			.startDate(cartItem.getStartDate())
			.price(cartItem.getProduct().getPrice())
			.build();
		orderItemRepo.save(orderItem);

		order.getOrderItems().add(orderItem);

		cartRepo.deleteById(cartItemId);

		return order;
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
}

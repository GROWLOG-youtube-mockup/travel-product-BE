package com.travelservice.domain.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.cart.entity.Cart;
import com.travelservice.domain.cart.repository.CartRepository;
import com.travelservice.domain.order.dto.OrderItemDto;
import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.order.entity.OrderItem;
import com.travelservice.domain.order.repository.OrderRepository;
import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.product.repository.ProductRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepo;
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
	public Order createOrderFromCart(String email) {
		User user = userRepo.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		List<Cart> cartItems = cartRepo.findByUser_UserId(user.getUserId());
		if (cartItems.isEmpty()) {
			throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
		}

		Order order = new Order();
		order.setUser(user);
		order.setOrderDate(LocalDateTime.now());

		int totalQty = 0;
		List<OrderItem> orderItems = new ArrayList<>();

		for (Cart cart : cartItems) {
			Product product = cart.getProduct();

			if (product.getStockQuantity() < cart.getQuantity()) {
				throw new CustomException(ErrorCode.OUT_OF_STOCK);
			}

			product.setStockQuantity(product.getStockQuantity() - cart.getQuantity());

			OrderItem item = OrderItem.builder()
				.order(order)
				.product(product)
				.peopleCount(cart.getQuantity())
				.startDate(cart.getStartDate())
				.price(product.getPrice()) // 현재 가격으로 저장
				.build();

			orderItems.add(item);
			totalQty += cart.getQuantity();
		}

		order.setOrderItems(orderItems);
		order.setTotalQuantity(totalQty);
		Order savedOrder = orderRepo.save(order);

		cartRepo.deleteAll(cartItems);

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
}

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepo;
	private final ProductRepository productRepo;
	private final UserRepository userRepo;
	private final CartItemRepository cartItemRepo;
	private final RedisTemplate<String, String> redisTemplate;

	@Transactional
	public Order createOrder(String email, List<OrderItemDto> itemDtos) {
		User user = userRepo.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("유저 없음");
		}

		Order order = new Order();
		order.setUser(user);
		order.setOrderDate(LocalDateTime.now());

		int totalQty = 0;
		for (OrderItemDto dto : itemDtos) {
			Product product = productRepo.findById(dto.getProductId())
				.orElseThrow(() -> new RuntimeException("상품 없음"));

			if (product.getStockQuantity() < dto.getQuantity()) {
				throw new RuntimeException("재고 부족");
			}

			product.setStockQuantity(product.getStockQuantity() - dto.getQuantity());

			OrderItem item = OrderItem.builder()
				.order(order)
				.product(product)
				.peopleCount(dto.getQuantity())
				.startDate(dto.getStartDate())
				.build();

			order.getItems().add(item);
			totalQty += dto.getQuantity();
		}
		order.setTotalQuantity(totalQty);
		Order savedOrder = orderRepo.save(order);
		redisTemplate.opsForValue().set(savedOrder.getOrderId().toString(), "dummy");
		return orderRepo.save(order);
	}

	@Transactional
	public Order createOrderFromCart(String email) {
		User user = userRepo.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("유저 없음");
		}

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

		order.setItems(orderItems);
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
		User user = userRepo.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("유저 없음");
		}
		return orderRepo.findByUser(user);
	}
}

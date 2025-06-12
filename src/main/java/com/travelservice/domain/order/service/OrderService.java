package com.travelservice.domain.order.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
			Product product = productRepo.findById(dto.getProduct().getId())
					.orElseThrow(() -> new RuntimeException("상품 없음"));
			if (product.getStockQuantity() < dto.getPeopleCount()) {
				throw new RuntimeException("재고 부족");
			}
			product.setStockQuantity(product.getStockQuantity() - dto.getPeopleCount());

			OrderItem item = OrderItem.builder()
					.order(order)
					.product(product)
					.peopleCount(dto.getPeopleCount())
					.startDate(dto.getStartDate())
					.build();
			order.getItems().add(item);
			totalQty += dto.getPeopleCount();
		}
		order.setTotalQuantity(totalQty);
		return orderRepo.save(order);
	}
}

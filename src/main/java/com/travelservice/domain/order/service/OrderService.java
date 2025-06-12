//package com.travelservice.domain.order.service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.travelservice.domain.order.dto.OrderItemDto;
//import com.travelservice.domain.order.entity.Order;
//import com.travelservice.domain.order.repository.OrderRepository;
//import com.travelservice.domain.product.repository.ProductRepository;
//import com.travelservice.domain.user.entity.User;
//import com.travelservice.domain.user.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//
//
//@Service
//@RequiredArgsConstructor
//public class OrderService {
//	private final OrderRepository orderRepo;
//	private final ProductRepository productRepo;
//	private final UserRepository userRepo;
//
//	@Transactional
//	public Order createOrder(String email, List<OrderItemDto> itemDtos) {
//		User user = userRepo.findByEmail(email);
//		if (user == null) {
//			throw new RunTimeException("유저 없음");
//		}
//
//		Order order = new Order();
//		order.setUser(user);
//		order.setOrderDate(LocalDateTime.now());
//
//		int totalQty = 0;
//
//	}
//}

package com.travelservice.domain.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.user.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUser(User user);

	List<Order> findByUser_UserId(Long userId);

	Optional<Order> findByOrderIdAndUser_UserId(Long orderId, Long userId);
}

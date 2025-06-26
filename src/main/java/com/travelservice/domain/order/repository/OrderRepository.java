package com.travelservice.domain.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.travelservice.domain.order.entity.Order;
import com.travelservice.domain.user.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUser(User user);

	List<Order> findByUser_UserId(Long userId);

	Optional<Order> findByOrderIdAndUser_UserId(Long orderId, Long userId);

	@Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.user.userId = :userId")
	List<Order> findByUser_UserIdWithItems(@Param("userId") Long userId);

	@Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.orderId = :orderId AND o.user.userId = :userId")
	Optional<Order> findByOrderIdAndUserIdWithItems(@Param("orderId") Long orderId, @Param("userId") Long userId);
}

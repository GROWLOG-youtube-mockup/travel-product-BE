package com.travelservice.domain.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.travelservice.domain.order.entity.OrderItem;

import io.lettuce.core.dynamic.annotation.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	@Query("""
			SELECT oi FROM OrderItem oi
			JOIN FETCH oi.product p
			LEFT JOIN FETCH p.images
			JOIN oi.order o
			WHERE o.user.userId = :userId
			AND o.status != com.travelservice.enums.OrderStatus.CANCELLED
		""")
	List<OrderItem> findByOrder_User_userId(@Param("userId") Long userId);

}

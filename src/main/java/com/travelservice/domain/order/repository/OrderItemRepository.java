package com.travelservice.domain.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	@EntityGraph(attributePaths = {"product", "product.images"})
	List<OrderItem> findByOrder_User_userId(Long userId);
}

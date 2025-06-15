package com.travelservice.domain.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelservice.domain.cart.entity.CartItem;
import com.travelservice.domain.user.entity.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	List<CartItem> findByUser(User user);
}

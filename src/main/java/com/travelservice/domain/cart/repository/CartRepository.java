package com.travelservice.domain.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelservice.domain.cart.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	List<Cart> findByUser_UserId(Long userId);

}

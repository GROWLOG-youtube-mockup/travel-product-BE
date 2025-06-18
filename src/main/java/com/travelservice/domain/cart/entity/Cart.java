package com.travelservice.domain.cart.entity;

import java.time.LocalDate;

import com.travelservice.domain.product.entity.Product;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_item")
public class Cart extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_item_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "quantity")
	@Min(1)
	private Integer quantity;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Builder
	public Cart(User user, Product product, Integer quantity, LocalDate startDate) {
		this.user = user;
		this.product = product;
		this.quantity = quantity;
		this.startDate = startDate;
	}
}



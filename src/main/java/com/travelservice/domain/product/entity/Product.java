package com.travelservice.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long productId;

	@Column(nullable = false)
	private String name;

	private Integer price;

	@Column(name = "total_quantity")
	private int totalQuantity;

	@Column(name = "stock_qantity")
	private int stockQuantity;

	@Lob
	private String description;

	@Column(columnDefinition = "TINYINT")
	private Integer type;

	@Column(columnDefinition = "TINYINT", nullable = false)
	private int saleStatus;
}
